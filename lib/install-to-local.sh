#!/bin/env bash
export PATH=$PATH:~/apps/apache-maven-3.6.0/bin
echo $PATH
get_abs_path() {
	PDIR=$(dirname $(readlink -f $0))
	#echo "1:"$PDIR
	#dirname $0|grep "^/" >/dev/null
	dirname $(dirname $(readlink -f $0))|grep "^/" >/dev/null
	if [ $? -eq 0 ];then
		#PDIR=`dirname $0`
		PDIR=$(dirname $(readlink -f $0))
		#echo "2:"$PDIR
	else
		#dirname $0|grep "^\." >/dev/null
		dirname $(readlink -f $0) | grep "^\." >/dev/null
		retval=$?
		echo "3:"$PDIR
		if [ $retval -eq 0 ];then
			#PDIR=`dirname $0|sed "s#^.#$PDIR#"`
			PDIR=`dirname $(readlink -f $0)|sed "s#^.#$PDIR#"`
		else
			#PDIR=`dirname $0|sed "s#^#$PDIR/#"`
			PDIR=`dirname $(readlink -f $0)|sed "s#^#$PDIR/#"`
		fi
	fi
	echo $PDIR
}
PDIR=`get_abs_path $0`
# APP_HOME=$(cd $PDIR; cd ..; pwd);
APP_HOME=$(cd $PDIR; pwd);

mvn install:install-file \
  -Dfile="$APP_HOME/asu-commands-1.0.0-SNAPSHOT.jar" \
  -DgroupId=me.asu \
  -DartifactId=asu-commands \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar
  #-DlocalRepositoryPath=path-to-specific-local-repo

mvn install:install-file \
  -Dfile="$APP_HOME/asu-util-1.0.3-SNAPSHOT.jar" \
  -DgroupId=me.asu \
  -DartifactId=asu-util \
  -Dversion=1.0.3-SNAPSHOT \
  -Dpackaging=jar