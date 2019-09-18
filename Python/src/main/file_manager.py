import os, shutil

class FileManager(object):
    @staticmethod
    def clear_dir(directory):
        for file in os.listdir(directory):
            file_path = os.path.join(directory, file)
            
            if os.path.isfile(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path): 
                shutil.rmtree(file_path)

    @staticmethod
    def write_to_file(file, open_option, string, new_line_begin, semicolon):
        tmp = str(string) + (';' if semicolon is True else '')
        
        if new_line_begin is not None:    
            tmp = '\n' + string + (';' if semicolon is True else '') if new_line_begin is True else string + (';\n' if semicolon is True else '\n')
    
        with open(file, open_option) as stats:
            stats.write(tmp)
