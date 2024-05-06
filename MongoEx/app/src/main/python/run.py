import math
from general import *
def encode(text):
    encoded = ''
    count = 1
    prev_char = ''

    if not text:
        return ''

    for char in text:
        if char != prev_char:
            if prev_char:
                encoded += str(count) + prev_char
            count = 1
            prev_char = char
        else:
            count += 1

    encoded += str(count) + prev_char
    return encoded

def decode(encoded):
    decoded = ''
    count = ''
    for char in encoded:
        if char.isdigit():
            count += char
        else:
            decoded += char * int(count)
            count = ''
    return decoded
