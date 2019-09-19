using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace TesterTools
{
    static class OutputManager
    {
        public static List <string> ProcessRawOutput(string solver, string rawOutput)
        {
            if(solver.Equals(SolverEnum.DLV))
                return ProcessDLVOutput(rawOutput);
            else
                return ProcessClingo_DLV2Output(rawOutput);
        }

        private static List <string> ProcessDLVOutput(string rawOutput)
        {
            List<string> sortedOutput = new List<string>();

            foreach (string str in Regex.Split(rawOutput, "\\r?\\n"))
            {
                if (str.Length <= 0)
                    continue;
                
                string[] tokens = Regex.Split(str.Substring(1, str.Length - 2), ",\\s+");
                
                Array.Sort(tokens);
                sortedOutput.Add(string.Join(" ", tokens));
            }
            
            return sortedOutput;
        }

        private static List<string> ProcessClingo_DLV2Output(string rawOutput)
        {
            List<string> sortedOutput = new List<string>();

            foreach (string str in Regex.Split(rawOutput, "\\r?\\n"))
            {
                if(str.Length <= 0)
                    continue;

                string[] tokens = Regex.Split(str, "\\s+");

                Array.Sort(tokens);
                sortedOutput.Add(string.Join(" ", tokens));
            }

            return sortedOutput;
        }
    }
}
