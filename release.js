const fs = require('fs');
const util = require('util');
const exec = util.promisify(require('child_process').exec);

module.exports = {
  prepare: async (config, context) => {
    const prevProperties = fs.readFileSync('./gradle.properties', { encoding: 'UTF-8' });
    const nextProperties = prevProperties.replace(/version=.+/, `version=${context.nextRelease.version}`);
    fs.writeFileSync('./gradle.properties', nextProperties);
    const { stdout, stderr } = await exec('./gradlew signPlugin');
    console.log(stdout);
    console.error(stderr);
  }
};
