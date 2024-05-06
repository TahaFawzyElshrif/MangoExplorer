import math
from general import *
import ast
def encode(text):
    """
    Encode Text 
    Args:
      text : string to encode  
    Returns
      code: String encoded (as bits)
     """
    dictionary_size = 256
    dictionary = {chr(i): i for i in range(dictionary_size)}
    result = []
    w = ""
    for c in text:
        wc = w + c
        if wc in dictionary:
            w = wc
        else:
            result.append(dictionary[w])
            dictionary[wc] = dictionary_size
            dictionary_size += 1
            w = c

    if w:         #for last character
        result.append(dictionary[w])
    return result

def decode(code):
    """
    Decode Text
    Args:
      code: String encoded (as bits)
    Returns
      text : original string 
     """

    if isinstance(code,str):
        code = ast.literal_eval(code)

    dictionary_size = 256
    dictionary = {i: chr(i) for i in range(dictionary_size)}
    result = []
    w = chr(code[0]) 
    result.append(w)
    entry = w
    for k in code[1:]:
        if k in dictionary:
            current_entry = dictionary[k]
        elif k == dictionary_size:
            current_entry = entry + entry[0] #for last element

        result.append(current_entry)
        dictionary[dictionary_size] = entry + current_entry[0]
        dictionary_size += 1
        entry = current_entry

    return ''.join(result)
