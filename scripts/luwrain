#!/bin/sh -e
# The Luwrain startup script
# Michael Pozhidaev <msp@altlinux.org>

MAIN_CLASS=org.luwrain.core.Init
USER_HOME_DIR=~
USER_FONTS_DIR="$USER_HOME_DIR/.luwrain/fonts"
USER_APPS_DIR="$USER_HOME_DIR/.luwrain/apps"
USER_CONF_DIR="$USER_HOME_DIR/.luwrain/conf"
if [ -z "$LUWRAIN_DIR" ]; then
    LUWRAIN_DIR=~/luwrain
fi
LUWRAIN_DATA_DIR="$LUWRAIN_DIR/data"
LUWRAIN_CONF_DIR="$LUWRAIN_DIR/conf"
LOG_FILE=/tmp/.luwrain.output

collect_jars()
{
    if [ -d "$1" ]; then
	find "$1" -iname '*.jar' | xargs echo | sed s/' '/':'/g | sed s:/./:/:g
    fi
}

collect_xml()
{
    if [ -d "$1" ]; then
	find "$1" -iname '*.xml' | xargs echo | sed s/' '/':'/g | sed s:/./:/:g
    fi
}

CLASS_PATH=":$(collect_jars "$LUWRAIN_DIR/lib/.")"
CLASS_PATH="$CLASS_PATH:$(collect_jars "$LUWRAIN_DIR/jar/.")"
CLASS_PATH="$CLASS_PATH:$(collect_jars "$USER_APPS_DIR/.")"

CONF_LIST="$(collect_xml "$LUWRAIN_CONF_DIR/.")"
CONF_LIST="$CONF_LIST:$(collect_xml "$USER_CONF_DIR/.")"

/bin/mkdir -p "$USER_FONTS_DIR"
/bin/mkdir -p "$USER_APPS_DIR"
if ! [ -d "$USER_CONF_DIR" ]; then
    /bin/mkdir -p "$USER_CONF_DIR"
    [ -d "$LUWRAIN_DIR/conf-local-default" ] && /bin/cp -r "$LUWRAIN_DIR/conf-local-default/." "$USER_CONF_DIR"
fi

exec java \
-cp "$CLASS_PATH" \
-Dsun.java2d.fontpath="$USER_FONTS_DIR" \
-Djava.library.path="$LUWRAIN_DIR/lib" \
"$MAIN_CLASS" \
--data-dir="$LUWRAIN_DATA_DIR" \
--user-home-dir="$USER_HOME_DIR" \
--conf-list="$CONF_LIST" \
"$@" &> "$LOG_FILE"
