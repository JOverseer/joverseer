#!/usr/bin/env bash
#

# FIX
brew install launch4j
brew cask install packages

export PATH="/usr/local/bin:$PATH"

if [ ! -h ../launch4j ]; then
	echo "Linking ../launch4j to brew installation"
	(cd ../ ;ln -s /usr/local/opt/launch4j/libexec launch4j )
fi

for i in txt2xmljar orderchecker joverseerupdaterjar joverseerjar; do
	(cd ../$i; ant )
done

ant -f build.xml MacOSInstaller

mkdir -p dist/bin

cp -R ../joverseerjar/resources/metadata dist/bin
