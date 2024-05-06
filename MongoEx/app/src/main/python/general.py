import math
import json
def calc_probability(text):
    char_d = {char: text.count(char) / len(text) for char in set(text)}
    return json.dumps(char_d)

def calc_entropy(text):
    letter_prob = calc_probability(text)
    if isinstance(letter_prob,str):
        letter_prob = json.loads(letter_prob)
    letter_prob = {key: float(value) for key, value in letter_prob.items()}

    entropy = sum([-i * math.log2(i) for i in letter_prob.values()])
    return entropy

def calc_avg_length(alpha_code, text):
    if isinstance(alpha_code,str):
        alpha_code = json.loads(alpha_code)
   # alpha_code = {key: list(value) for key, value in alpha_code.items()}

    letter_prob = calc_probability(text)

    if isinstance(letter_prob,str):
        letter_prob = json.loads(letter_prob)
    letter_prob = {key: float(value) for key, value in letter_prob.items()}

    avg_length = sum([len(alpha_code[char]) * letter_prob[char] for char in letter_prob])
    return avg_length

def calc_efficiency(alpha_code, text):
    if isinstance(alpha_code,str):
        alpha_code = json.loads(alpha_code)
    #alpha_code = {key: list(value) for key, value in alpha_code.items()}

    avg_length = calc_avg_length(alpha_code, text)
    entropy = calc_entropy(text)
    return (entropy / avg_length) * 100

def calc_CR_DATA(before, after):#file info is constant before ,and after ,so dismiss it (at least in this version)
    bits_before = len(before) * 8 #this version assume systemEncode 8  (ASCII)
    bits_after = len(after)   # Assuming each character is encoded as 8 bits
    compression_ratio = bits_before  / bits_after
    return compression_ratio
