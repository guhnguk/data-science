#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import csv

with open("./Demographic_Statistics_By_Zip_Code.csv", newline="") as infile:
    data = list(csv.reader(infile))

    count_participants_index = data[0].index("COUNT PARTICIPANTS")


import statistics

count_participants = [int(row[count_participants_index]) for row in data[1:]]
print(statistics.mean(count_participants), statistics.stdev(count_participants))