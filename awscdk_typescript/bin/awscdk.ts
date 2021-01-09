#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { AwscdkStack } from '../lib/awscdk-stack';

const myenv = {account: process.env.CDK_DEFAULT_ACCOUNT, 
                region: process.env.CDK_DEFAULT_REGION}

const app = new cdk.App();
new AwscdkStack(app, 'AwscdkStack', {env: myenv});
