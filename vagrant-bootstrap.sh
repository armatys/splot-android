#!/bin/bash

set -e
# sudo apt-get --assume-yes install libc6-dev-i386
wget http://dl.google.com/android/ndk/android-ndk32-r10-linux-x86.tar.bz2
tar xjvf android-ndk32-r10-linux-x86.tar.bz2
rm android-ndk32-r10-linux-x86.tar.bz2

echo 'export PATH=$HOME/android-ndk-r10:$PATH' >> ~/.bash_profile

sudo apt-get update
sudo apt-get --assume-yes install default-jdk
