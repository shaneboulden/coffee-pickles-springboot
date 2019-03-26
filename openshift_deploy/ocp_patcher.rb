require 'yaml'
require 'optparse'
require 'base64'

options = {}
opt_parser = OptionParser.new do |opt|
  opt.banner = "Usage: ruby ocp_patcher.rb -k=KEYSTORE_PASS -t=TRUSTSTORE_PASS DC.YAML"

  opt.on "-k=KEYSTORE","--keystore=KEYSTORE","Keystore Pass" do |key|
    options[:consumer_key] = key
  end

  opt.on "-t=TRUSTSTORE","--truststore=TRUSTSTORE","Truststore Pass" do |secret|
    options[:consumer_secret] = secret
  end
end.parse!

file = YAML::load_file(ARGV[0])

# create the openshift secret
keystore64 = Base64.strict_encode64(options[:keystore_pass])
truststore64 = Base64.strict_encode64(options[:truststore_pass])
`oc create secret generic keystore --from-file=../tls/final/server.keystore`
`oc create secret generic truststore --from-file=../tls/final/server.truststore`
secret_json = {"apiVersion"=>"v1",
               "kind"=>"Secret",
               "metadata"=>{
                 "name"=>"java-secrets",
                 "type"=>"Opaque",
                 "label"=>"tripper"
               },
               "data"=>{
                 "keystore-pass"=>"#{keystore64}",
                 "truststore-pass"=>"#{truststore64}"
                }
              }
`cat <<EOF | oc create -f - 
#{secret_json.to_yaml}
EOF`

# patch the dc
secretEnv = [{
             "name"=>"KEYSTORE_PASS",
             "valueFrom"=>{
               "secretKeyRef"=>{
                 "name"=>"java-secrets",
                 "key"=>"keystore-pass"
               }
             }
            },
            {"name"=>"TRUSTSTORE_PASS",
               "valueFrom"=>{
                 "secretKeyRef"=>{
                   "name"=>"java-secrets",
                   "key" => "truststore-pass"
                 }
               }
            },
            {"name"=>"KEY_PASS",
               "valueFrom"=>{
                 "secretKeyRef"=>{
                   "name"=>"java-secrets",
                   "key" => "keystore-pass"
                 }
               }
            },
            {"name"=>"PSQL_USER",
              "valueFrom"=>{
                "secretKeyRef"=>{
                  "name"=>"postgresql",
                  "key"=>
]

# create the volume mounts
volumes = [{"name"=>"keystore",
            "secret"=>{
             "secretName"=>"keystore"
            }
           },
           {"name"=>"truststore",
            "secret"=>{
              "secretName"=>"truststore"
            }
           }]
file["spec"]["template"]["spec"]["volumes"] = volumes
# create the volume mounts
volumeMounts = [{"name"=>"keystore",
                "mountPath"=>"/etc/pki/server.keystore",
                "readOnly"=>"true"
                },
                {"name"=>"truststore",
                "mountPath"=>"/etc/pki/server.truststore",
                "readOnly"=>"true"
                }]
file["spec"]["template"]["spec"]["containers"][0]["volumeMounts"] = volumeMounts

file["spec"]["template"]["spec"]["containers"][0]["env"] = secretEnv
File.open(ARGV[0],'w') {|f| f.write file.to_yaml }
