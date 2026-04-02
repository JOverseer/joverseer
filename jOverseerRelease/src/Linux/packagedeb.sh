#!/bin/sh
# run in jOverseerRelease
PACKAGE_NAME="joverseer"
PACKAGE_VERSION="1.19"
WORK_DIR="dist"
CONTROL="$WORK_DIR/debian/DEBIAN/control"
RELEASE_SRC=src/Linux
RELEASE_DIST=dist
CONTROL_SRC=$RELEASE_SRC/control


mkdir -p $WORK_DIR/debian/DEBIAN
mkdir -p $WORK_DIR/debian/lib
mkdir -p $WORK_DIR/debian/bin
mkdir -p $WORK_DIR/debian/usr/share/applications
mkdir -p $WORK_DIR/debian/usr/share/doc/$PACKAGE_NAME

echo "Package: $PACKAGE_NAME">$CONTROL
echo "Version: $PACKAGE_VERSION">>$CONTROL
cat $CONTROL_SRC >>$CONTROL

cp $RELEASE_SRC/$PACKAGE_NAME.desktop $WORK_DIR/debian/usr/share/applications/
cp $RELEASE_SRC/LICENCE $WORK_DIR/debian/usr/share/doc/$PACKAGE_NAME/copyright

cp -r $RELEASE_DIST/ $WORK_DIR/debian/lib/$PACKAGE_NAME

gzip -9c $WORK_DIR/debian/lib/$PACKAGE_NAME/NEWS >$WORK_DIR/debian/usr/share/doc/$PACKAGE_NAME

mv $WORK_DIR/debian/lib/$PACKAGE_NAME/resources/$PACKAGE_NAME.svg $WORK_DIR/debian/usr/share/doc
chmod 644 $WORK_DIR/debian/usr/share/doc/$PACKAGE_NAME.svg

cp $RELEASE_SRC/unix-launcher.sh $WORK_DIR/debian/bin/$PACKAGE_NAME
chmod 755 $WORK_DIR/debian/bin/$PACKAGE_NAME

PACKAGE_SIZE=`du -bs $WORK_DIR/debian | cut -f 1`
PACKAGE_SIZE=$((PACKAGE_SIZE/1024))
echo "Installed-Size: $PACKAGE_SIZE">>$CONTROL

chown -R root $WORK_DIR/debian/
chgrp -R root $WORK_DIR/debian/

cd $WORK_DIR
dpkg --build debian
mv debian.deb $PACKAGE_NAME-$PACKAGE_VERSION.deb

