#/bin/bash
# This shell is to swap with an argument
cd /etc/nginx
sed -e 's?http:.*?http://'$1':8080/activity/;?' <nginx.conf > /tmp/xxx
cp /tmp/xxx nginx.conf
service nginx reload
