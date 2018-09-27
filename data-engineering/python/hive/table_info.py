#!/usr/bin/env python
# -*- coding:utf-8 -*-
__author__ = 'Ryan Guhnguk Ahn'


import sys


import logging
import commands


logging.basicConfig(filename='./table_info.log', level=logging.DEBUG)

console = logging.StreamHandler()
console.setLevel(logging.DEBUG)

# formatter = logging.Formatter('%(name)-12s: %(levelname)-8s %(message)s')
# tell the handler to use this format
# console.setFormatter(formatter)
# add the handler to the root logger
logging.getLogger('').addHandler(console)


# hive -e 'show create table magic_t1411006.plnt_ko' >> hive-ddl.txt 2> /dev/null; echo ';' >> hive-ddl.txt
# hadoop distcp webhdfs://10.10.204.42:14000/user/hive/warehouse/hkmc_tms.db/dtc_rg_mnt webhdfs://10.12.4.111:14000/user/hive/warehouse/hkmc_tms.db

cli_header = "hive -e \'show create table "
cli_footer1 = "\' >> "
cli_footer2 = " 2> /dev/null; echo \';\' >> "


def desc_table_info(info_file):
    tb_info = info_file[1]
    sv_file = info_file[2]
    try:
        read_file = open(tb_info, "r")
        tables = read_file.readlines()
        for table in tables:
            table = table.strip("\n")
            cmd = cli_header + "hkmc_tms." + table + cli_footer1 + sv_file + cli_footer2 + sv_file
            logging.info(cmd)
            # result = commands.getoutput(cmd)
            # logging.info(result)
        read_file.close()
    except IOError:
        ""


if __name__ == "__main__":
    f = sys.argv
    desc_table_info(f)

