# Directory size exporter for Prometheus

## Requirements
- Java 17 or later

```
# On Ubuntu / Debian
sudo apt install openjdk-17-jre

# On macOS
brew install openjdk@17
```

## Usage

Download and extract with:
```
wget https://github.com/daniellehrner/directory-size-exporter/releases/latest/download/directory-size-exporter.tar.gz
tar xfz directory-size-exporter.tar.gz
```

A config file is needed to specify the directories. For example:
```json
{
  "directories": [
    "/var/lib/besu",
    "/var/lib/teku"
  ]
}
```

A systemd service can be used to start the exporter:
```
[Unit]
Description=Exports size of directories to Prometheus
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=prometheus
Group=prometheus
Restart=always
RestartSec=5
ExecStart=<PATH_TO_DOWNLOAD>/bin/directory-size-exporter <PATH_TO_CONFIG>/directory-size-exporter.json

[Install]
WantedBy=multi-user.target
```

Finally Promotheus needs to be configured to scrape the exporter. For example:
```yaml
scrape_configs:
  - job_name: "directory-size-exporter"
    metrics_path: /metrics
    static_configs:
      - targets: ['localhost:9101']
```

The examples above can also be found on the `examples` directory.