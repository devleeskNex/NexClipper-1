# Run

```
docker run -d \
	     --name NexClipper \
	     -p 10001:8080 \
	     --volume {directory_path_where_to_maintain_data}:/var/h2 \
	     --volume /var/run/docker.sock:/var/run/docker.sock \
	     sppark/nexclipper:v1
```

# Example

```
docker run -d \
	     --name NexClipper \
	     -p 10001:8080 \
	     --volume /Users/uengine/docker/h2:/var/h2 \
	     --volume /var/run/docker.sock:/var/run/docker.sock \
	     sppark/nexclipper:v1
```


