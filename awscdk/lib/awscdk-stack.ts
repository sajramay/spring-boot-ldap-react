import * as cdk from '@aws-cdk/core';
import * as ec2 from '@aws-cdk/aws-ec2';
import { countResources } from '@aws-cdk/assert';
import { CfnOutput } from '@aws-cdk/core';

export class AwscdkStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // The code that defines your stack goes here
    const vpc = ec2.Vpc.fromLookup(this, 'myvpc', {
      isDefault: true,
    })

    new CfnOutput(this, 'myvpcoutput', {
      value:vpc.vpcId
    })
  }
}
