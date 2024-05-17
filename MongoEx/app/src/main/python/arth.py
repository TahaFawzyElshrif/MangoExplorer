import json
from general import *
import math
class ArithmeticEncoder:
    def __init__(self, symbols, probabilities):
        self.symbols = (symbols)
        self.probabilities = (probabilities)

    def encode(self, message):
        low = 0.0
        high = 1.0

        for symbol in message:
            index = self.symbols.index(symbol)
            symbol_low = low + (high - low) * sum(self.probabilities[:index])
            symbol_high = low + (high - low) * sum(self.probabilities[:index + 1])
            low = symbol_low
            high = symbol_high

        encoded_value = (low + high) / 2

        return encoded_value

def decode(encoded_value, symbols, probabilities, message_length):
    decoded_message = ""
    low = 0.0
    high = 1.0

    for _ in range(message_length):
        symbol = None
        for i, prob in enumerate(probabilities):
            symbol_low = low + (high - low) * sum(probabilities[:i])
            symbol_high = low + (high - low) * sum(probabilities[:i + 1])
            if symbol_low <= encoded_value < symbol_high:
                symbol = symbols[i]
                low = symbol_low
                high = symbol_high
                break

        decoded_message += symbol

    return decoded_message


def ArithmeticCompress(text):
    prob_dic = calc_probability(text)

    if isinstance(prob_dic,str):
        prob_dic = json.loads(prob_dic)
    prob_dic = dict({key: float(value) for key, value in prob_dic.items()})

    encoder = ArithmeticEncoder(list(prob_dic.keys()), list(prob_dic.values()))
    return encoder.encode(text)


def ArithmeticDecompress(encoded,prob_dic,len_message):
    if isinstance(prob_dic,str):
        prob_dic = json.loads(prob_dic)
    if isinstance(encoded,str):
        encoded = float(encoded)
    if isinstance(len_message,str):
        len_message = int(len_message)

    prob_dic = {key: float(value) for key, value in prob_dic.items()}
   
    decoded_message = decode(encoded, list(prob_dic.keys()), list(prob_dic.values()), len_message)
    return decoded_message



