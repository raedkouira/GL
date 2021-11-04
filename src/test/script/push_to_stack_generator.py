#!usr/bin/python3

def add_and_mult(n):
    if n == 0:
        return "1+1"
    else:
        return "1 + ({})".format(add_and_mult(n-1))

def main():
    with open("test_too_much_registers.deca", 'w') as test_file:
        test_file.write("// @result \n")
        test_file.write("{\n")
        big_expression = add_and_mult(17)
        test_file.write("int x = " + big_expression + ";\n")
        test_file.write("print(x);\n")
        test_file.write("}")

main()