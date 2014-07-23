#!/bin/sh
cp -R target/batch-notifications-1.0.0.BUILD-SNAPSHOT-bin/modules/* $XD_HOME/modules
cp src/main/resources/data/paymentImport/payment.input /tmp/payment.input
