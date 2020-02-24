from os import listdir
from os.path import isfile, join
import os, shutil

class FileManager(object):
    @staticmethod
    def clear_dir(directory):
        for file in os.listdir(directory):
            if file == '__init__.py':
                continue

            file_path = os.path.join(directory, file)
            
            if os.path.isfile(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path): 
                shutil.rmtree(file_path)

    @staticmethod
    def solver_present(solvers_dir, solver_name):
        for file in listdir(solvers_dir):
            if isfile(join(solvers_dir, file)) and file == solver_name:
                return True
        
        return False

    @staticmethod
    def read_filters(file_name, match_solver):
        with open(file_name) as file:
            content = file.readlines()
        
        filters = [];
        
        for fil in content:
            if fil.startswith(match_solver + ':'):
                filters.append(fil[fil.index(':') + 1:].strip())
        
        return filters 

    @staticmethod
    def write_to_file(file, open_option, string, new_line_begin, semicolon):
        tmp = str(string) + (';' if semicolon is True else '')
        
        if new_line_begin is not None:    
            tmp = '\n' + string + (';' if semicolon is True else '') if new_line_begin is True else string + (';\n' if semicolon is True else '\n')
    
        with open(file, open_option) as stats:
            stats.write(tmp)
