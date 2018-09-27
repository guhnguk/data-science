__author__ = 'Ryan Ahn'

from os import listdir
import os.path

import tf_idf
import fileio
import cosine_similarity

if __name__ == "__main__":
    path = r"D:\Workspace\Git\snippet-python\tm\data\alices_adventures_in_wonderland.txt"
    doc_path = r"D:\Workspace\Git\snippet-python\tm\data\\"

    file_list = listdir(doc_path)

    for f in file_list:
        print (f)
        extension = os.path.splitext(f)[1]
        if extension != ".txt":
            continue

        analyzed_dict = tf_idf.tf_idf_analyze(doc_path, doc_path + f)

        for cmp_file in file_list:
            print(f + " and " + cmp_file)
            cmp_dict = tf_idf.tf_idf_analyze(doc_path, doc_path + cmp_file)

            cos_dict = cosine_similarity.consine_similarity_analyze(analyzed_dict, cmp_dict)
            print("Analyze[" + str(f.replace(path.splitext(f)[1], "")) + "," + str(cmp_file) + "]: " + str(cos_dict))

        write_f = f.replace(path.splitext(f)[1], ".csv")
        to_write_path = r"D:\Workspace\Git\snippet-python\tm\analyze\\"

        fileio.write_file(to_write_path + write_f, analyzed_dict)

