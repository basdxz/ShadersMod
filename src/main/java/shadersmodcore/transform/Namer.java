package shadersmodcore.transform;

import java.util.ArrayList;

public class Namer {
   ArrayList ac = new ArrayList();
   ArrayList af = new ArrayList();
   ArrayList am = new ArrayList();

   Names.Clas c(String name) {
      Names.Clas x = new Names.Clas(name);
      if (this.ac != null) {
         this.ac.add(x);
      }

      return x;
   }

   Names.Fiel f(Names.Clas clas, String name, String desc) {
      Names.Fiel x = new Names.Fiel(clas, name, desc);
      if (this.af != null) {
         this.af.add(x);
      }

      return x;
   }

   Names.Fiel f(Names.Clas clas, Names.Fiel fiel) {
      Names.Fiel x = new Names.Fiel(clas, fiel.name, fiel.desc);
      if (this.af != null) {
         this.af.add(x);
      }

      return x;
   }

   Names.Meth m(Names.Clas clas, String name, String desc) {
      Names.Meth x = new Names.Meth(clas, name, desc);
      if (this.am != null) {
         this.am.add(x);
      }

      return x;
   }

   Names.Meth m(Names.Clas clas, Names.Meth meth) {
      Names.Meth x = new Names.Meth(clas, meth.name, meth.desc);
      if (this.am != null) {
         this.am.add(x);
      }

      return x;
   }

   public void setNames() {
   }
}
