# kill all except the proxy
# docker rm -f $(docker ps -a -q -l)
echo "Removing old images"
docker rm -f $(docker ps -a | grep -v "ecs189_proxy" | awk '{print $1}' | grep -v "CONTAINER")

echo "Starting $1"
# Start the new image with given parameter as name
docker run -dP --net ecs189_default --name $1 $1

echo "Swapping images"
# Swap given the image name
docker exec ecs189_proxy_1 /bin/bash /bin/swap3.sh $1
