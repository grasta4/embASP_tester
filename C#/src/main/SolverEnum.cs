using it.unical.mat.embasp.platforms.desktop;
using it.unical.mat.embasp.specializations.clingo.desktop;
using it.unical.mat.embasp.specializations.dlv.desktop;
using System;

namespace TesterTools
{
    static class SolverEnum
    {
        public const string CLINGO = "clingo", DLV = "dlv";

        public static string GetOutputOption(string solver)
        {
            switch (solver)
            {
                case CLINGO: return "--verbose=0";
                case DLV: return "-silent";
                default: Environment.Exit(exitCode: -1); return null;
            }
        }

        public static DesktopService GetService(string solver)
        {
            switch (solver)
            {
                case CLINGO: return new ClingoDesktopService("../../rsc/" + solver + ".solver");
                case DLV: return new DLVDesktopService("../../rsc/" + solver + ".solver");
                default: Environment.Exit(exitCode: -1); return null;
            }
        }

        public static string[] GetSolvers()
        {
            return new string[] { CLINGO, DLV };
        }
    }
}
