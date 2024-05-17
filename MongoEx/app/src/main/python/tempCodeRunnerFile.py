from arth import *
from general import *
import json
import random
random.seed(42)
prob_dic = calc_probability("a a b a")
print(prob_dic)
encoded=ArithmeticCompress("a a b a")
print("Encoded value:", encoded)
