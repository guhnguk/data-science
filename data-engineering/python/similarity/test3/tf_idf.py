__author__ = 'Ryan Ahn'


def term_frequency(term, document):
    normalize_document = document.lower().split()
    doc_cnt = normalize_document.count(term.lower)
    doc_number = float(len(normalize_document))

    return doc_cnt / doc_number


if __name__=="__main__":
    doc1 = "The game of life is a game of everlasting learning"
    doc2 = "The unexamined life is not worth living"
    doc3 = "Never stop learning"

    print (term_frequency("game", doc1))
