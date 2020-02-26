using it.unical.mat.embasp.platforms.desktop;
using it.unical.mat.embasp.specializations.clingo.desktop;
using it.unical.mat.embasp.specializations.dlv.desktop;
using System;

namespace EmbASP_Tester
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

        public static DesktopService GetService(string solversFolder, string solver)
        {
            string tmp = solversFolder + solver + ".solver";

            switch (solver)
            {
                case CLINGO: return new ClingoDesktopService(tmp);
                case DLV: return new DLVDesktopService(tmp);
                default: Environment.Exit(exitCode: -1); return null;
            }
        }

        public static string[] GetSolvers()
        {
            return new string[] { CLINGO, DLV };
        }
    }
}
