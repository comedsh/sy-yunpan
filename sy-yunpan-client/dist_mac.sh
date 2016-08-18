#!/usr/bin/env bash

export PATH=${PATH}:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin
#export PATH=/Library/Java/JavaVirtualMachines/jdk1.8.0_102.jdk/Contents/Home/bin
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_102.jdk/Contents/Home/

CURDIR=`PWD`
echo $CURDIR

#CLASSPATH1=""

#for file in `ls build/install/sy-yunpan-client/lib`
#  do
    #    if [ -d $file ]
    #then
		#	CLASSPATH1=$CLASSPATH1
    #else
		#	CLASSPATH1=$CLASSPATH1:$file
    #fi    
#done

#echo $CLASSPATH1

javafxpackager -deploy \
	-BbundleArgument=-Duser.dir=acdd \
	-BjvmProperties="-Duser.dir=acdd" \
	-Bbundler-argument=-Duser.dir=acdd \
	-BjvmOptions=-Duser.dir=acdd \
	-argument "helloworld" \
	-Bargument=-Duser.dir=acdd \
	-Bargument=hello=world \
	-title "yunpan" \
	-name "yunpan" \
	-appclass org.shangyang.yunpan.client.Client \
	-native dmg \
	-outdir $CURDIR/build/install/out \
	-outfile yunpan \
	-srcdir $CURDIR/build/install/sy-yunpan-client/lib \

# a goodle sample there https://gist.github.com/jewelsea/5018976
# mount the installer disk image	
# hdiutil attach bundles/HelloWorld-1.0.dmg