#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import json

object = """
{
    "name": "식빵",
    "family": "웰시코기",
    "age": 1,
    "weight": 2.14
}
"""

print(object)

with open("data.json", "w") as out_json:
    json.dump(object, out_json, indent=None, sort_keys=False)

with open("./data.json") as in_json:
    object1 = json.load(in_json)

json_string = json.dumps(object1)
print(json_string)

object2 = json.loads(json_string)
print(object2)