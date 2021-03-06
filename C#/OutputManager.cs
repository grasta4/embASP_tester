﻿using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace EmbASP_Tester
{
    static class OutputManager
    {
        public static List <string> ProcessRawOutput(string solver, string rawOutput)
        {
            if(solver.Equals(SolverEnum.DLV))
                return ProcessDLVOutput(rawOutput);
            else
                return ProcessClingoOutput(rawOutput);
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

        private static List<string> ProcessClingoOutput(string rawOutput)
        {
            List<string> sortedOutput = new List<string>();

            foreach (string str in Regex.Split(rawOutput, "\\r?\\n"))
            {
                if(str.Length <= 0)
                    continue;

                List <string> tokens = new List <string> (Regex.Split(str, "\\.?\\s+"));

                tokens.RemoveAll(token => string.IsNullOrWhiteSpace(token));
                tokens.Sort((a, b) => a.CompareTo(b));
                sortedOutput.Add(string.Join(" ", tokens).Trim());
            }

            return sortedOutput;
        }
    }
}
