#!/bin/bash
ENV="qa"
BROWSER="chrome"
DRIVER_PATH=/usr/local/bin/chromedriver

sbt -Dbrowser=$BROWSER -Denvironment=$ENV -Dwebdriver.chrome.driver=${DRIVER_PATH} 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerQA'