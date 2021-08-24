#!/bin/bash
# ===============================================
# Github release upload script by Enginecrafter77
# ===============================================

GRADLE_OUTPUTS="build/libs"
CURL_COMMAND="curl"
if [ ! -z ${DRY_RUN} ]
then
	CURL_COMMAND="echo ${CURL_COMMAND}"
	GITHUB_TOKEN="######"
else
	if [ -z ${GITHUB_TOKEN} ]
	then
		echo "ERROR: Missing github token" >&2
		exit 1
	fi
fi

if grep -qv "refs/tags/.*" <<< ${GITHUB_REF}
then
	if [ ! -z ${DRY_RUN} ]
	then
		echo "WARNING: Unspecified github reference" >&2
		if [ -z ${GITHUB_REF} ]
		then
			release_tag="v1.0.0"
		else
			release_tag="$(basename ${GITHUB_REF})"
		fi
	else
		echo "ERROR: Current tree doesn't have corresponding release" >&2
		exit 1
	fi
else
	release_tag=$(cut -c 11- <<< ${GITHUB_REF})
fi

gradle_provided_version=$(./gradlew -q version | cut -d ' ' -f 2)
binary_name=$(ls ${GRADLE_OUTPUTS} | grep -vE "^*-sources.jar$" | grep ${gradle_provided_version})
binary_path=${GRADLE_OUTPUTS}/${binary_name}

http_response=$(${CURL_COMMAND} -s --ssl -X POST -H "Authorization: token ${GITHUB_TOKEN}" --upload-file "${binary_path}" --write-output "%{http_code}" --output /dev/null "https://uploads.github.com/repos/${GITHUB_REPOSITORY}/releases/tag/${release_tag}/assets?name=${binary_name}")

if [ ! -z ${DRY_RUN} ]
then
	echo -e "gradle-provided-version: ${gradle_provided_version}\nbinary-name: ${binary_name}\nbinary-path: ${binary_path}\n${http_response}"
	exit 0
fi

if [ ! "${http_response}" = "200" ]
then
	echo "ERROR: HTTP code ${http_response}" >&2
	exit 1
fi
