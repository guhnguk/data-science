__author__ = 'Ryan Ahn'

from math import pow, sqrt


def consine_similarity(vector_a, vector_b, vector_dot):

    a_vector_magitude = 0
    for elem in vector_a:
        a_vector_magitude += pow(elem, 2)

    b_vector_magnitude = 0
    for elem in vector_b:
        b_vector_magnitude += pow(elem, 2)

    magnitude = sqrt(a_vector_magitude) + sqrt(b_vector_magnitude)
    dot_product = 0

    for d in vector_dot:
        dot_product += d

    return dot_product / magnitude


def consine_similarity_analyze(document_dict_a, document_dict_b):

    cmp_len_a = len(document_dict_a)
    cmp_len_b = len(document_dict_b)

    if cmp_len_b < cmp_len_a:
        cmp_len_a = cmp_len_b

    # comp_len = len(document_dict_a)
    # if len(document_dict_b) < cmp_len:
    #     cmp_len = len(document_dict_b)

    vector_a = []
    vector_b = []
    vector_dot = []

    for v in document_dict_a.values():
        vector_a.append(v)

    for v in document_dict_b.values():
        vector_b.append(v)

    for a_k, a_v in document_dict_a.items():
        if a_k in document_dict_b:
            vector_dot.append(a_v * document_dict_b[a_k])

    return consine_similarity(vector_a, vector_b, vector_dot)

