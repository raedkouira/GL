#!usr/bin/python3

def main():
    with open("test_stack_overflow.deca", 'w') as test_file:
        test_file.write("// @result Error: Stack overflow\n")
        test_file.write("{\n")
        for char1 in range(97, 123):
            for char2 in range(97, 123):
                for char3 in range(97, 123):
                    test_file.write("int {}{}{} = 0;\n".format(chr(char1), chr(char2), chr(char3)))
        test_file.write("}")

main()