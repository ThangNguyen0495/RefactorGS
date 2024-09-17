#!/bin/bash

# Define the path to the config.properties file
CONFIG_FILE="src/main/resources/config.properties"

# Create the properties file with environment variables
cat <<EOL > $CONFIG_FILE
# Secret Variables
buyerAccount=${BUYER_ACCOUNT}
buyerPassword=${BUYER_PASSWORD}
sellerAccount=${SELLER_ACCOUNT}
sellerPassword=${SELLER_PASSWORD}

# Environment Variables
apiHost=${API_HOST}
browser=${BROWSER}
domain=${DOMAIN}
sfEndpoint=${SF_ENDPOINT}
headless=${HEADLESS}
EOL

echo "Generated $CONFIG_FILE"
