#!/usr/bin/env python
# -*- coding:utf-8 -*-
__author__ = 'Ryan Guhnguk Ahn'


import sys


distcp_header = "hadoop distcp -i -delete -overwrite"
batch_cluster = "webhdfs://bmnode01:14000"
interactive_cluster = "webhdfs://imnode01:14000"


def do_distcp(path_info):
    src_path = path_info[1]
    dst_path = path_info[2]

    if src_path[0] != "/":
        src_path = "/" + src_path

    if dst_path[0] != "/":
        dst_path = "/" + dst_path

    cmd = distcp_header + " " + batch_cluster + src_path + " " + interactive_cluster + dst_path
    print cmd

    # logging.info(cmd)
    # result = commands.getoutput(cmd)
    # logging.info(result)


if __name__ == "__main__":
    path_info = sys.argv

    if len(path_info) != 3:
        print "Usage: batch_interactive_distcp.py <source_path> <target_path>"

    do_distcp(path_info)
