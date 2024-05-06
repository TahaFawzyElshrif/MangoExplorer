import json
from general import *
def getDictFrom(dec,i):
    newDic=dict({})
    for i in range(i,len(dec)):
        newDic[list(dec.keys())[i]]=list(dec.values())[i]
    return newDic
class node:
    def __init__(self, data, prop,code="-1"):#-1 not have code
        self.left = None
        self.right = None
        self.data = data
        self.prop = prop
        self.code = code
    def __repr__(self) :
        return f"Node {self.data} , prob :{self.prop}, code :{self.code}"

    def __lt__(self, other):
        return self.prop < other.prop

def getAlgorithm(sorted_char_prop,root,code):#start from 1(root prob) then take one node(large possible prob) node ,other node has prob of root_prob-large
    if(not list(sorted_char_prop.keys())):
        return root

    first_node=node(list(sorted_char_prop.keys())[0],list(sorted_char_prop.values())[0])
    second_node=node("",round(root.prop-first_node.prop,1))
    #round(,1) to prevent expression like .6-.2 to return .399999999997 ,to just be .4

    if (len(sorted_char_prop)==0):#base-case
        return (root,code)

    if(root.data!=''):#if so ,it's leaf node
        code[root.data]=root.code
        return (root,code)

    if(len(list(sorted_char_prop.keys()))==1):# if just exist one ,not divide
        root.data=list(sorted_char_prop.keys())[0]
        code[root.data]=root.code
        return (root,code)

    if (first_node<second_node):
        second_node.code=(root.code+"0" if root.code!="-1" else "0")
        first_node.code=(root.code+"1" if root.code!="-1" else "1")
        root.left=second_node
        root.right=first_node
        root.left=getAlgorithm(getDictFrom(sorted_char_prop,1),root.left,code)
        root.right=getAlgorithm(getDictFrom(sorted_char_prop,1),root.right,code)

    else:
        second_node.code=(root.code+"1" if root.code!="-1" else "1")
        first_node.code=(root.code+"0" if root.code!="-1" else "0")
        root.left=first_node
        root.right=second_node
        root.left=getAlgorithm(getDictFrom(sorted_char_prop,1),root.left,code)
        root.right=getAlgorithm(getDictFrom(sorted_char_prop,1),root.right,code)






    return (root,code)





def getAlgorithmCode(text):
    char_prop=calc_probability(text)
    if isinstance(char_prop,str):
        char_prop = json.loads(char_prop)
    char_prop = {key: float(value) for key, value in char_prop.items()}

    sorted_char_prop= dict(sorted(char_prop.items(), key=lambda item: item[1], reverse=True))
    Code=dict({})
    root,Code=getAlgorithm(sorted_char_prop,node("",1),Code)
    return json.dumps(Code)
def encode(text):
    Code=getAlgorithmCode(text)
    if isinstance(Code,str):
        Code = json.loads(Code)
    encoded=''.join([Code[i] for i in text])
    return encoded

def decode(encoded_message, mapping):
    if isinstance(mapping,str):
        mapping = json.loads(mapping)
    reversed_mapping = {code: char for char, code in mapping.items()}
    current_code = ''
    decoded_message = ''
    for bit in encoded_message:
        current_code += bit
        if current_code in reversed_mapping:
            char = reversed_mapping[current_code]
            decoded_message += char
            current_code = ''
    return decoded_message


