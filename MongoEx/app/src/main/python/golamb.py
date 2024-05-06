
import math
from  general import *
def encode(value,M):
    """
    Encode value
    Args:
      value : int to encode
    Returns
      code: String encoded (as bits)
     """
    value=int(value)#if passed string parse it ,generaly safe to use

    # Handle special case when value is 0.
    if value == 0:
        M = 1

    quotient = value // M
    remainder = value % M

    # Encode quotient in unary.
    unary = '1' * quotient + '0'

    # Handle special case when M is 1.
    if M == 1:
        return unary

    # Encode remainder in binary.
    binary_length = math.ceil(math.log2(M))
    x = 2**binary_length - M
    # Check if M is not a power of 2 and the remainder is not within the range [0, x-1].
    if (math.log2(M) % 1 != 0) and not (remainder >= 0 and remainder <= x-1):
        binary = format(remainder + x, f'0{binary_length}b')
    else:
        binary_length = math.floor(math.log2(M))
        binary = format(remainder, f'0{binary_length}b')

    return unary + binary

# Utility function to check if the string is represented in binary system.

def decode(code,M):
    """
    Decode Text
    Args:
      code: String encoded (as bits)
    Returns
      text : original string
     """


    # Decode the unary part to quotient.
    unary_length = code.find('0') + 1
    quotient = unary_length - 1

    # Handle special case when M is 1.
    if M == 1:
        return quotient

        # Decode remainder in binary.
    binary_part = code[unary_length:]
    ceil_log2M = math.ceil(math.log2(M))
    # Check if M is not a power of 2 and if the binary part fits within ceil(log2(M)) bits.
    if (math.log2(M) % 1 != 0) and (len(binary_part) == ceil_log2M):
        x = 2**ceil_log2M - M
        remainder = int(binary_part, 2) - x
    else:
        remainder = int(binary_part, 2)

    return quotient * M + remainder