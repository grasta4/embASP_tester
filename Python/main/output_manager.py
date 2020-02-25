import re

class OutputManager(object):
    @staticmethod
    def process_raw_output(solver, raw_output):
        if solver == 'dlv':
            return OutputManager.__process_DLV_output(raw_output)
        else:
            return OutputManager.__process_Clingo_DLV2_output(raw_output)

    @staticmethod
    def __process_DLV_output(raw_output):
        sorted_output = []
        
        for str_ in re.compile('\\r?\\n').split(raw_output):
            tokens = re.compile(',\\s+').split(str_[1:-1])
            
            tokens.sort()
            sorted_output.append(' '.join(tokens))
        
        return sorted_output

    @staticmethod
    def __process_Clingo_DLV2_output(raw_output):
        sorted_output = []

        for str_ in re.compile('\\r?\\n').split(raw_output):
            tokens = re.compile('\.?\\s+').split(str_)

            for token in tokens:
                if not token:
                    tokens.remove(token)

            tokens.sort()
            sorted_output.append((' '.join(tokens)).strip())
        
        return sorted_output
