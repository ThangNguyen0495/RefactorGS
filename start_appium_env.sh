#!/bin/bash

set -e

# Args
SIM_NAME="$1"
IOS_VERSION="$2"

# Colors
GREEN='\033[0;32m'
NC='\033[0m'

echo "Installing Appium..."
npm install -g appium

echo "Installing Appium XCUI Test Driver..."
appium driver install xcuitest

echo "Installing Carthage..."
brew install carthage

echo -e "${GREEN}Carthage installed${NC}"

echo "Starting Appium server in background..."
nohup nohup appium -a 0.0.0.0 -p 4723 -pa /wd/hub --relaxed-security> appium_log.txt 2>&1 &
sleep 5

echo -e "${GREEN}Appium started${NC}"

echo "Creating simulator for '$SIM_NAME' with iOS $IOS_VERSION..."
UDID=$(xcrun simctl create "$SIM_NAME" "com.apple.CoreSimulator.SimDeviceType.iPhone-15" "com.apple.CoreSimulator.SimRuntime.iOS-${IOS_VERSION}")

echo "Booting simulator $SIM_NAME..."
xcrun simctl boot "$UDID" || echo "(Simulator may already be booted)"

echo "Installing WebDriverAgent on simulator..."
cd ~/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent

LOG_FILE="wda_build.log"
rm -f "$LOG_FILE"

xcodebuild -project WebDriverAgent.xcodeproj \
  -scheme WebDriverAgentRunner \
  -destination "platform=iOS Simulator,id=$UDID" \
  DEVELOPMENT_TEAM="" \
  CODE_SIGN_IDENTITY="" \
  CODE_SIGNING_REQUIRED=NO \
  CODE_SIGNING_ALLOWED=NO \
  test > "$LOG_FILE" 2>&1 &

echo "Waiting for WebDriverAgent build to finish..."

while ! grep -q "ServerURLHere->http://" "$LOG_FILE"; do
  sleep 10
  echo "Waiting for WebDriverAgent build to finish..."
done

echo "${GREEN} Build appears to be finished${NC}"
tail -n 20 "$LOG_FILE"
