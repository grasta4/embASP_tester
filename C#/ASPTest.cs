using it.unical.mat.embasp.@base;
using it.unical.mat.embasp.languages.asp;
using it.unical.mat.embasp.platforms.desktop;
using Microsoft.CSharp;
using System;
using System.CodeDom.Compiler;
using System.Collections.Generic;
using System.IO;

namespace EmbASP_Tester
{
    public static class ASPTest
    {
        public static void ImportClasses(string[] classes, string embASPLib)
        {
            foreach (string file in classes)
                if (file.EndsWith(".cs"))
                {
                    CompilerParameters parameters = new CompilerParameters
                    {
                        GenerateInMemory = true
                    };

                    parameters.ReferencedAssemblies.Add(embASPLib);

                    CompilerResults results = new CSharpCodeProvider().CompileAssemblyFromSource(parameters, File.ReadAllText(file));

                    Console.WriteLine(results.Errors);

                    foreach (Type type in results.CompiledAssembly.GetTypes())
                        ASPMapper.Instance.RegisterClass(type);
                }
        }

        public static void LoadClasses(Type[] classes)
        {
            foreach (Type cls in classes)
                ASPMapper.Instance.RegisterClass(cls);
        }

        private static SortedSet<string> SortFacts(ISet<object> answerSet)
        {
            SortedSet<string> sorted = new SortedSet<string>();

            foreach (object atom in answerSet)
                sorted.Add(atom.ToString());

            return sorted;
        }

        public static void Run(string csvFile, string solversDir, string inputFile, string optionFile)
        {
            FileManager.WriteToFile(csvFile, inputFile, true, true);

            foreach (string solver in SolverEnum.GetSolvers())
            {
                if (!FileManager.SolverPresent(solversDir, solver + ".solver"))
                    continue;

                ASPInputProgram inputProgram = new ASPInputProgram();
                DesktopHandler handler = new DesktopHandler(SolverEnum.GetService(solversDir, solver));
                List<ISet<object>> answerSets = new List<ISet<object>>();
                int optionID = handler.AddOption(new OptionDescriptor(SolverEnum.GetOutputOption(solver)));

                if (string.IsNullOrWhiteSpace(optionFile))
                    FileManager.ReadFilters(optionFile, solver).ForEach(filter => handler.AddOption(new OptionDescriptor(" " + filter)));

                handler.AddProgram(inputProgram);
                inputProgram.AddFilesPath(inputFile);

                List<string> sortedOutput = OutputManager.ProcessRawOutput(solver, ((AnswerSets)(handler.StartSync())).OutputString);

                handler.RemoveOption(optionID);

                var watch = System.Diagnostics.Stopwatch.StartNew();

                foreach (AnswerSet answerSet in ((AnswerSets)(handler.StartSync())).Answersets)
                    answerSets.Add(answerSet.Atoms);

                watch.Stop();
                FileManager.WriteToFile(csvFile, solver + ":" + watch.ElapsedMilliseconds.ToString(), null, true);

                if (answerSets.Count > 0)
                    foreach (ISet<object> answerSet in answerSets)
                    {
                        string tmp = string.Join(" ", SortFacts(answerSet));

                        if (!sortedOutput.Contains(tmp))
                            Console.WriteLine("ERROR! Original " + solver + " output does not contain:\n" + tmp + "\n"); ;
                    }
            }

            Console.WriteLine("END");
        }
    }
}