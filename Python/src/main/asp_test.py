from base.option_descriptor import OptionDescriptor
from enum import Enum
from languages.asp.asp_mapper import ASPMapper
from languages.asp.asp_input_program import ASPInputProgram
from languages.predicate import Predicate
from platforms.desktop.desktop_handler import DesktopHandler
from specializations.clingo.desktop.clingo_desktop_service import ClingoDesktopService
from specializations.dlv.desktop.dlv_desktop_service import DLVDesktopService
#from specializations.dlv2.desktop.dlv2_desktop_service import DLV2DesktopService
from main.file_manager import FileManager
from main.output_manager import OutputManager
import importlib, inspect, os, re, shutil, sys, time

CLASSES_PATH = '../classes/'
EXECUTION_TIMES_PATH = '../../files/executionTimes.csv'

class Solver(Enum):
    CLINGO = 'clingo'
    DLV = 'dlv'
    #DLV2 = 'dlv2'
    
    def get_output_option(self):
        if self is Solver.CLINGO:
            return '--verbose=0'
        elif self is Solver.DLV:
            return '-silent'
        #elif self is Solver.DLV2:
        #    return '--competition-output'
        
    def get_service(self):
        if self is Solver.CLINGO:
            return ClingoDesktopService('../../rsc/' + solver.value +'.solver')
        elif self is Solver.DLV:
            return DLVDesktopService('../../rsc/' + solver.value +'.solver')
        #elif self is Solver.DLV2:
        #    return DLV2DesktopService('../../rsc/' + solver.value +'.solver')

    @staticmethod
    def get_solvers():
        return [Solver.CLINGO, Solver.DLV]

def load_classes(mapper):
    for file in os.listdir(CLASSES_PATH):
        if file.endswith('.py'):
            for cls in inspect.getmembers(importlib.import_module('classes.' + file[:-3]), inspect.isclass):
                if cls[1] is not Predicate:
                    mapper.register_class(cls[1])

def sort_facts(answer_set):
    sorted_ = []
    
    for atom in answer_set:
        sorted_.append(str(atom))
    
    sorted_.sort()
    
    return sorted_

if __name__ == "__main__":
    if len(sys.argv) <= 1:
        print('USAGE: ASPTest input_file class1 [class2...]');
        sys.exit(0);
    
    for index in range(2, len(sys.argv)):
        shutil.copyfile('../../files/' + sys.argv[index], CLASSES_PATH + re.search('[^/]+$', sys.argv[index]).group())
    
    load_classes(ASPMapper.get_instance())
    FileManager.write_to_file(EXECUTION_TIMES_PATH, 'a', sys.argv[1], True, True)
    
    for solver in Solver.get_solvers():
        answer_sets = []
        input_program = ASPInputProgram()
        handler = DesktopHandler(Solver.get_service(solver))
        option_id = handler.add_option(OptionDescriptor(solver.get_output_option()))
        
        handler.add_program(input_program)
        input_program.add_files_path('../../files/' + sys.argv[1])
        
        sorted_output = OutputManager.process_raw_output(solver.value, handler.start_sync().get_answer_sets_string())
        
        handler.remove_option_from_id(option_id)
        
        start_time = time.time()
        
        for answer_set in handler.start_sync().get_answer_sets():
            answer_sets.append(answer_set.get_atoms())
        
        end_time = time.time()
        
        FileManager.write_to_file(EXECUTION_TIMES_PATH, 'a', end_time - start_time, None, True)
        
        if answer_sets:
            for answer_set in answer_sets:
                tmp = ' '.join(sort_facts(answer_set))
                
                if tmp not in sorted_output:
                    print('ERROR! Original ' + solver.value + ' output does not contain:\n' + tmp + '\n')
        
    FileManager.clear_dir(CLASSES_PATH)
