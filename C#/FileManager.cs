using System.Collections.Generic;
using System.IO;

namespace EmbASP_Tester
{ 
    static class FileManager
    {
        public static List <string> ReadFilters(string fileName, string matchSolver)
        {
            List<string> filters = new List<string>();

            string[] lines = File.ReadAllLines(fileName);

            foreach(string line in lines)
                if(line.StartsWith(matchSolver + ":"))
                    filters.Add(line.Substring(line.IndexOf(':') + 1).Trim());

            return filters;
        }

        public static bool SolverPresent(string solversDir, string solverName)
        {
            return File.Exists(solversDir + solverName);
        }

        public static void WriteToFile(string file, string line, bool? newLineBegin, bool semicolon)
        {
            string tmp = line + (semicolon ? ";" : "");

            if (newLineBegin != null)
                tmp = (bool)newLineBegin ? "\r\n" + line + (semicolon ? ";" : "") : line + (semicolon ? ";\r\n" : "\r\n");
            
            using (var tw = new StreamWriter(file, true))
                tw.Write(tmp);
        }
    }
}