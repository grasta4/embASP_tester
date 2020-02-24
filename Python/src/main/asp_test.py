from base.option_descriptor import OptionDescriptor
from enum import Enum
from languages.asp.asp_mapper import ASPMapper
from languages.asp.asp_input_program import ASPInputProgram
from languages.predicate import Predicate
from platforms.desktop.desktop_handler import DesktopHandler
from specializations.clingo.desktop.clingo_desktop_service import ClingoDesktopService
from specializations.dlv.desktop.dlv_desktop_service import DLVDesktopService
from specializations.dlv2.desktop.dlv2_desktop_service import DLV2DesktopService
from file_manager import FileManager
from output_manager import OutputManager
import importlib, inspect, os, re, shutil, sys, time

CLASSES_PATH = '../classes/'

class Solver(Enum):
    CLINGO = 'clingo'
    DLV = 'dlv'
    DLV2 = 'dlv2'
    
    def get_output_option(self):
        if self is Solver.CLINGO:
            return '--verbose=0'
        elif self is Solver.DLV:
            return '-silent'
        elif self is Solver.DLV2:
            return '--competition-output'
        
    def get_service(self, solver_folder):
        if self is Solver.CLINGO:
            return ClingoDesktopService(solver_folder + self.value + '.solver')
        elif self is Solver.DLV:
            return DLVDesktopService(solver_folder + self.value + '.solver')
        elif self is Solver.DLV2:
            return DLV2DesktopService(solver_folder + self.value + '.solver')

    @staticmethod
    def get_solvers():
        return [Solver.CLINGO, Solver.DLV, Solver.DLV2]

def load_classes(mapper):
    for file in os.listdir(CLASSES_PATH):
        if file.endswith('.py'):
            for cls in inspect.getmembers(importlib.import_module(file[:-3]), inspect.isclass):
                if cls[1] is not Predicate:
                    mapper.register_class(cls[1])

def sort_facts(answer_set):
    sorted_ = []
    
    for atom in answer_set:
        sorted_.append(str(atom))
    
    sorted_.sort()
    
    return sorted_

def run(csv_file, solvers_dir, input_file, option_file, classes):
    for index in range(0, len(classes)):
        shutil.copyfile(classes[index], CLASSES_PATH + re.search(r'[^/]+$', classes[index]).group())

    load_classes(ASPMapper.get_instance())
    FileManager.write_to_file(csv_file, 'a', input_file, True, True)

    for solver in Solver.get_solvers():
        if not FileManager.solver_present(solvers_dir, solver.value + '.solver'):
            continue

        answer_sets = []
        input_program = ASPInputProgram()
        handler = DesktopHandler(solver.get_service(solvers_dir))
        option_id = handler.add_option(OptionDescriptor(solver.get_output_option()))

        if option_file is not None and option_file != "" and option_file.lower() != "-no-option-file":
            for _filter in FileManager.read_filters(option_file, solver):
                handler.add_option(OptionDescriptor(' ' + _filter))

        handler.add_program(input_program)
        input_program.add_files_path(input_file)

        sorted_output = OutputManager.process_raw_output(solver.value, handler.start_sync().get_answer_sets_string())

        handler.remove_option_from_id(option_id)

        start_time = time.time()

        for answer_set in handler.start_sync().get_answer_sets():
            answer_sets.append(answer_set.get_atoms())

        end_time = time.time()

        FileManager.write_to_file(csv_file, 'a', solver.value + ':' + str(end_time - start_time), None, True)

        if answer_sets:
            for answer_set in answer_sets:
                tmp = ' '.join(sort_facts(answer_set))

                if tmp not in sorted_output:
                    print('ERROR! Original ' + solver.value + ' output does not contain:\n' + tmp + '\n')

    FileManager.clear_dir(CLASSES_PATH)
    print('END')
