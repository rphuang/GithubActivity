# kill the latest image
docker rm -f $(docker ps -a -q -l)

# Start the new image with given parameter as name
docker run -dP --net ecs189_default --name $1 $1


# Swap given the image name
docker exec ecs189_proxy_1 /bin/bash /bin/swap3.sh $1
