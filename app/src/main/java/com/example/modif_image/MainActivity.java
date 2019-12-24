package com.example.modif_image;

import androidx.appcompat.app.AppCompatActivity;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptC;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inMutable = true;
        o.inScaled = false;
        final Bitmap bm_c = BitmapFactory.decodeResource(getResources(), R.drawable.mq_ilet,o);
        final Bitmap bm_DE = BitmapFactory.decodeResource(getResources(), R.drawable.contrast_faible,o);
        final Bitmap bm_NoirBlanc_HE = BitmapFactory.decodeResource(getResources(), R.drawable.noir_blanc_che,o);
        final Bitmap bm = bm_c;
        final ImageView im = findViewById(R.id.imTest);

        final int height = bm.getHeight();
        final int width= bm.getWidth();

        final int[] colors = new int[bm.getHeight()*bm.getWidth()];
        bm.getPixels(colors,0,width,0,0,width,height);


        //affichage de la taille de la bipmap.
        TextView tv = findViewById(R.id.size);
        tv.setText("height = "+height+"\n"+"width = "+width);

        final Button reset=findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r,g,b;
                for (int i=0; i<colors.length;i++){
                    r=Color.red(colors[i]);
                    g=Color.green(colors[i]);
                    b=Color.blue(colors[i]);
                    colors[i]=Color.rgb(r,g,b);
                }
                bm.setPixels(colors,0,width,0,0,width,height);
                im.setImageBitmap(bm);
            }
        });

        Button image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setImageBitmap(bm_c);
            }
        });

        Button imageDE = findViewById(R.id.imageDE);
        imageDE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setImageBitmap(bm_DE);
            }
        });

        Button imageHE = findViewById(R.id.grayImageHE);
        imageHE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setImageBitmap(bm_NoirBlanc_HE);
            }
        });

        Button gray = findViewById(R.id.button_gray);
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayRS(bm);
                im.setImageBitmap(bm);
            }
        });

        Button hue = findViewById(R.id.hue);
        hue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorize(bm);
                im.setImageBitmap(bm);
            }
        });

        Button effect = findViewById(R.id.effect);
        effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effect(bm);
                im.setImageBitmap(bm);
            }
        });


        Button contrast=findViewById(R.id.gray_contrast);
        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contrastDE(bm);
                im.setImageBitmap(bm);
            }
        });

        Button reverseContrast = findViewById(R.id.reverseContrast);
        reverseContrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revContrastDE(bm,192,53);
                im.setImageBitmap(bm);

            }
        });

        Button contrastHE = findViewById(R.id.contrastHE);
        contrastHE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContrastHE(bm);
                im.setImageBitmap(bm);
            }
        });



    }


    private void invertRS(Bitmap bmp){

        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_invert invertScript = new ScriptC_invert(rs);

        invertScript.forEach_toInvert(input,output);

        output.copyTo(bmp);

        input.destroy() ; output.destroy() ;
        invertScript.destroy(); rs.destroy();
    }


    //conversion en noir et blanc
    /*recupère chaque pixel et modifi sa couleur un à un*/
    public void toGray(Bitmap bmp){
        int r, g, b, gray, pixel ;
        for (int i = 0; i<bmp.getWidth();i=i+1){
            for(int j =0; j<bmp.getHeight();j=j+1){
                pixel=bmp.getPixel(i,j);
                r=Color.red(pixel);
                g=Color.green(pixel);
                b=Color.blue(pixel);
                gray = (int) (r*0.3)+(int) (g*0.59)+(int) (b*0.11);
                bmp.setPixel(i,j,Color.rgb(gray,gray,gray));
            }
        }
    }
    /*Création d'une liste où chaque élément est la "couleur" d'un pixel (getPixels())
    Modification des éléments de la liste pour obtenir leurs équivalent en noir et blanc
    Remplacement des pixels de la bipmap avec les couleurs de la liste (setPixels())*/
    public void toGray2(Bitmap bmp){
        int r, g, b, gray;
        int width = bmp.getWidth(), height=bmp.getHeight();
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);
        for (int i=0; i<colors.length;i++){
            r=Color.red(colors[i]);
            g=Color.green(colors[i]);
            b=Color.blue(colors[i]);
            gray = (int) (r*0.3)+(int) (g*0.59)+(int) (b*0.11);
            colors[i]=Color.rgb(gray,gray,gray);
        }
        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    public void toGrayRS(Bitmap bmp){
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        grayScript.destroy(); rs.destroy();
    }

    public void colorize(Bitmap bmp){
        int r, g, b;
        //float[] hsv=new float[3];
        Random rdt = new Random();
        float hue = (float) rdt.nextInt(360);

        int width = bmp.getWidth();
        int height=bmp.getHeight();
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        for (int i=0; i<colors.length;i++) {
            r = Color.red(colors[i]);
            g = Color.green(colors[i]);
            b = Color.blue(colors[i]);
            //Color.colorToHSV(Color.rgb(r,g,b),hsv);
            float[] hsv=rgbToHsv(r,g,b);
            hsv[0]= hue;
            //colors[i]=Color.HSVToColor(hsv);
            colors[i]=hsvToRgb(hsv);
        }
        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //conversions de rgb à hsv et inversement.
    public float max(float a, float b, float c){
        return Math.max(a,Math.max(b,c));
    }
    public float min(float a, float b, float c){
        return Math.min(a,Math.min(b,c));
    }


    public float[] rgbToHsv(int red, int green, int blue){
        float[] hsv=new float[3];
        float H,S,V;

        float red2=red/255.f;
        float green2=green/255.f;
        float blue2=blue/255.f;

        float max=max(red2,green2,blue2);
        float min=min(red2,green2,blue2);
        float Delta = max-min;

        if (max==red2){
            H=60*(((green2-blue2)/Delta) % 6);
        }
        else {
            if(max==green2){
                H=60*(((blue2-red2)/Delta) +2);
            }
            else{
                if(max==blue2){
                    H=60*(((red2-green2)/Delta)+4);
                }
                else{
                    H=0;
                }
            }
        }
        hsv[0]=H;
        if(max==0){
            S=0;
        }
        else{
            S=Delta/max;
        }
        hsv[1]=S;

        V=max;
        hsv[2]=V;

        return hsv;
    }

    public int hsvToRgb(float[] hsv){
        int r, g, b;
        float C, X, m;
        float r2=0; float g2=0; float b2=0;
        float H=hsv[0];
        float S=hsv[1];
        float V=hsv[2];

        C=V*S;
        X=C*(1-Math.abs(((H/60)%2)-1));
        m=V-C;

        if(H>=0 && H<60){
            r2=C;
            g2=X;
            b2=0;
        }
        else{
            if (H>=60 && H<120){
                r2=X;
                g2=C;
                b2=0;
            }
            else{
                if(H>=120 && H<180){
                    r2=0;
                    g2=C;
                    b2=X;
                }
                else{
                    if(H>=180 && H<240){
                        r2=0;
                        g2=X;
                        b2=C;
                    }
                    else{
                        if(H>=240 && H<300){
                            r2=X;
                            g2=0;
                            b2=C;
                        }
                        else{
                            if(H>=300 && H<360){
                                r2=C;
                                g2=0;
                                b2=X;
                            }
                        }
                    }
                }
            }
        }
        r = (int)((r2+m)*255);
        g = (int)((g2+m)*255);
        b = (int)((b2+m)*255);
        return Color.rgb(r,g,b);
    }

    //effet de "mise en avant" d'une couleur
    public void effect(Bitmap bmp){ //laisser le choix à l'utilisateur
        int r, g, b, gray;
        int width = bmp.getWidth(), height=bmp.getHeight();
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        for(int i=0; i<colors.length; i++){
            r=Color.red(colors[i]);
            g=Color.green(colors[i]);
            b=Color.blue(colors[i]);

            if(g>155 || g<=b || g<=r){
                gray=(int) (r*0.3)+(int) (g*0.59)+(int) (b*0.11);
                colors[i]=Color.rgb(gray,gray,gray);
            }
        }
        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //Contraste noir et blanc (extention dynamique)
    public int max_ng(int[] colors){
        int m=Color.red(colors[0]);
        int c_r;
        for(int c:colors){
            c_r=Color.red(c);
            if(m<c_r){
                m=c_r;
            }
        }
        return m;
    }

    public int min_ng(int[] colors){
        int m=Color.red(colors[0]);
        int c_r;
        for(int c:colors){
            c_r=Color.red(c);
            if(m>c_r){
                m=c_r;
            }
        }
        return m;
    }

    public void grayContrastDE(Bitmap bmp){
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        int min_ng=min_ng(colors);
        int max_ng=max_ng(colors);

        int gray;
        for(int i=0;i<colors.length;i++){
            gray=Color.red(colors[i]);
            gray=(255*(gray-min_ng))/(max_ng-min_ng);
            colors[i]=Color.rgb(gray,gray,gray);
        }
        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    public void grayContrastDE2(Bitmap bmp){
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] LUT=new int[256];
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        int min_ng=min_ng(colors);
        int max_ng=max_ng(colors);

        for(int i=0;i<256;i++){
            LUT[i]= (int)(255.f*(i-min_ng))/(max_ng-min_ng);
        }

        int c_r;
        int gray;
        for(int i=0;i<colors.length;i++){
            c_r=Color.red(colors[i]);
            gray=LUT[c_r];
            colors[i]=Color.rgb(gray,gray,gray);
        }

        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    public void grayRevContrastDE(Bitmap bmp, int max, int min){
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] LUT=new int[256];
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        for(int i=0;i<256;i++){
            LUT[i]= (int)((i*(max-min))/255.f)+min;
        }

        int c_r;
        int gray;
        for(int i=0;i<colors.length;i++){
            c_r=Color.red(colors[i]);
            gray=LUT[c_r];
            colors[i]=Color.rgb(gray,gray,gray);
        }

        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //Contraste couleur (extention dynamique)
    public int max_r(int[] colors){
        int m=Color.red(colors[0]);
        int c_r;
        for(int c:colors){
            c_r=Color.red(c);
            if(m<c_r){
                m=c_r;
            }
        }
        return m;
    }

    public int max_g(int[] colors){
        int m=Color.green(colors[0]);
        int c_g;
        for(int c:colors){
            c_g=Color.green(c);
            if(m<c_g){
                m=c_g;
            }
        }
        return m;
    }

    public int max_b(int[] colors){
        int m=Color.blue(colors[0]);
        int c_b;
        for(int c:colors){
            c_b=Color.blue(c);
            if(m<c_b){
                m=c_b;
            }
        }
        return m;
    }

    public int min_r(int[] colors){
        int m=Color.red(colors[0]);
        int c_r;
        for(int c:colors){
            c_r=Color.red(c);
            if(m>c_r){
                m=c_r;
            }
        }
        return m;
    }

    public int min_g(int[] colors){
        int m=Color.green(colors[0]);
        int c_g;
        for(int c:colors){
            c_g=Color.green(c);
            if(m>c_g){
                m=c_g;
            }
        }
        return m;
    }

    public int min_b(int[] colors){
        int m=Color.blue(colors[0]);
        int c_b;
        for(int c:colors){
            c_b=Color.blue(c);
            if(m>c_b){
                m=c_b;
            }
        }
        return m;
    }

    public void contrastDE(Bitmap bmp){
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] LUTr=new int[256];
        int[] LUTg=new int[256];
        int[] LUTb=new int[256];
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        int min_r=min_r(colors);
        int max_r=max_r(colors);

        int min_g=min_g(colors);
        int max_g=max_g(colors);

        int min_b=min_b(colors);
        int max_b=max_b(colors);

        for(int i=0;i<256;i++){
            LUTr[i]= (int)(255.f*(i-min_r))/(max_r-min_r); //traiter le cas où l'image est uniforme (max=min)
            LUTg[i]= (int)(255.f*(i-min_g))/(max_g-min_g);
            LUTb[i]= (int)(255.f*(i-min_b))/(max_b-min_b);
        }

        int r,g,b;
        for(int i=0;i<colors.length;i++){
            r=Color.red(colors[i]);
            g=Color.green(colors[i]);
            b=Color.blue(colors[i]);
            colors[i]=Color.rgb(LUTr[r],LUTg[g],LUTb[b]);
        }

        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //augmentation du contraste (reverse contrast)
    public void revContrastDE(Bitmap bmp, int max, int min){
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] LUT=new int[256];
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        for(int i=0;i<256;i++){
            LUT[i]= (int)((i*(max-min))/255.f)+min;
        }

        int c_r,c_g,c_b;
        for(int i=0;i<colors.length;i++){
            c_r=Color.red(colors[i]);
            c_g=Color.green(colors[i]);
            c_b=Color.blue(colors[i]);
            colors[i]=Color.rgb(LUT[c_r],LUT[c_g],LUT[c_b]);
        }

        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //Contraste noir et blanc (Egalisation d’histogramme)
    public void grayContrastHE(Bitmap bmp){
        int gray;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] colors = new int[width*height];
        bmp.getPixels(colors,0,width,0,0,width,height);

        int[] hist=new int[256]; //faire attention au "int" pour les grosses valeures
        for(int i=0;i<256;i++){
            hist[i]=0;
        }

        for(int i=0;i<colors.length;i++){
            hist[Color.red(colors[i])]++;
        }

        for(int i=1;i<256;i++){
            hist[i]+=hist[i-1];
        }
        for(int i=0;i<colors.length;i++){
            gray = (hist[Color.red(colors[i])]*255)/hist[255];
            colors[i]=Color.rgb(gray,gray,gray);
        }
        bmp.setPixels(colors,0,width,0,0,width,height);
    }

    //Contraste couleur (Egalisation d’histogramme)
    public void ContrastHE(Bitmap bmp){ //faire avec hsv et pas rgb

        int r,g,b;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] colors = new int[width*height];
        float[][] hsv = new float[colors.length][3];
        bmp.getPixels(colors,0,width,0,0,width,height);

        float[] hist= new float[360]; //faire attention au "int" pour les grosses valeures
        for(int i=0;i<360;i++){
            hist[i]=0;
        }

        for(int i=0;i<colors.length;i++){
            r = Color.red(colors[i]);
            g = Color.green(colors[i]);
            b = Color.blue(colors[i]);
            hsv[i] = rgbToHsv(r,g,b);
            hist[((int)hsv[i][0])+180]++;
        }

        for(int i=1;i<360;i++){
            hist[i]+=hist[i-1];
        }
        for(int i=0;i<colors.length;i++){
            hsv[i][0] = (hist[((int)hsv[i][0])+180]*359)/hist[359];
            colors[i]=hsvToRgb(hsv[i]);
        }
        bmp.setPixels(colors,0,width,0,0,width,height);

    }

}
