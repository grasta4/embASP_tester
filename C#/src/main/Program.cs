using it.unical.mat.embasp.languages.asp;
using it.unical.mat.embasp.platforms.desktop;
using Microsoft.CSharp;
using System;
using System.CodeDom.Compiler;
using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;

class ASPTest
{
    private const string CLASSES_PATH = "../../files/classes/", EXECUTION_TIMES_PATH = "../../files/executionTimes.csv";
    
    private static void LoadClasses(ASPMapper mapper)
    {
        foreach(string file in Directory.GetFiles(CLASSES_PATH))
            if(file.EndsWith(".cs"))
            {
                CompilerParameters parameters = new CompilerParameters();
                parameters.GenerateInMemory = true;

                parameters.ReferencedAssemblies.Add("EmbASP.dll");

                CompilerResults results = new CSharpCodeProvider().CompileAssemblyFromSource(parameters, File.ReadAllText(file));

                Console.Write(results.Errors);
                    
                foreach (Type type in results.CompiledAssembly.GetTypes())
                    mapper.RegisterClass(type);
            }
    }

    private static SortedSet <string> SortFacts(ISet <object> answerSet)
    {
        SortedSet<string> sorted = new SortedSet <string> ();

        foreach (object atom in answerSet)
            sorted.Add(atom.ToString());

        return sorted;
    }

    static void Main(string[] args)
    {
        if(args.Length <= 1)
        {
            Console.WriteLine("USAGE: ASPTest input_file class1 [class2...]");
            Console.ReadKey();
            Environment.Exit(exitCode: 0);
        }

        for(int i = 1; i < args.Length; i++)
            File.Copy("../../files/" + args[i], CLASSES_PATH + new Regex("[^/]+$").Match(args[i]).Groups[0].Value);
        
        LoadClasses(ASPMapper.Instance);
        TesterTools.FileManager.WriteToFile(EXECUTION_TIMES_PATH, args[0], true, true);

        foreach (string solver in TesterTools.SolverEnum.GetSolvers())
        {
            ASPInputProgram inputProgram = new ASPInputProgram();
            DesktopHandler handler = new DesktopHandler(TesterTools.SolverEnum.GetService(solver));
            List <ISet<object>> answerSets = new List<ISet<object>>();
            int optionID = handler.AddOption(new it.unical.mat.embasp.@base.OptionDescriptor(TesterTools.SolverEnum.GetOutputOption(solver)));

            handler.AddProgram(inputProgram);
            inputProgram.AddFilesPath("../../files/" + args[0]);

            List<string> sortedOutput = TesterTools.OutputManager.ProcessRawOutput(solver, ((AnswerSets)(handler.StartSync())).OutputString);

            handler.RemoveOption(optionID);

            var watch = System.Diagnostics.Stopwatch.StartNew();
                
            foreach(AnswerSet answerSet in ((AnswerSets)(handler.StartSync())).Answersets)
                answerSets.Add(answerSet.Atoms);

            watch.Stop();
            TesterTools.FileManager.WriteToFile(EXECUTION_TIMES_PATH, (watch.ElapsedMilliseconds / 1000).ToString(), null, true);

            if(answerSets.Count > 0)
                foreach(ISet<object> answerSet in answerSets) {
                    string tmp = string.Join(" ", SortFacts(answerSet));

                    if(!sortedOutput.Contains(tmp))
                        Console.WriteLine("ERROR! Original " + solver + " output does not contain:\n" + tmp + "\n"); ;
                }
        }

        TesterTools.FileManager.ClearDir(CLASSES_PATH);
        Console.ReadKey();
    }
}
