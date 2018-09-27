__author__ = 'Ryan Ahn'


def read_file(path):
    f = open(path, mode="r")

    text = ""
    while True:
        line = f.readline()
        if (not line):
            break
        text += line
    return text


def write_file(path, dict):
    file = open(path, "w", encoding="UTF-8")
    for k, v in dict.item():
        text = str(k) + "," + str(v) + "\n"
        file.write(text)
    file.close()


if __name__ == "__main__":
    path = r"D:\Workspace\Git\snippet-python\tm\data\alices_adventures_in_wonderland.txt"
    contents = read_file(path)
    print(contents)