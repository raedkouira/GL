#!usr/bin/python3

def or_recursive(n):
    if n == 0:
        return "(2 < 1) || (1 < 2)"
    else:
        return "(2 < 1) || ({})".format(or_recursive(n-1))

def main():
    with open("test_too_much_registers_or.deca", 'w') as test_file:
        test_file.write("// @result ok\n")
        test_file.write("// @result ok\n")
        test_file.write("{\n")
        big_expression = or_recursive(17)
        test_file.write("boolean x = " + big_expression + ";\n")
        
        test_file.write("if ({}) ".format("x"))
        test_file.write("{\n")
        test_file.write("println(\"ok\");\n")
        test_file.write("} else {\n")
        test_file.write("println(\"ko\");\n")
        test_file.write("}")
        
        test_file.write("if ({}) \n".format(big_expression))
        test_file.write("{\n")
        test_file.write("println(\"ok\");\n")
        test_file.write("} else {\n")
        test_file.write("println(\"ko\");\n")
        test_file.write("}")


        test_file.write("}")


main()