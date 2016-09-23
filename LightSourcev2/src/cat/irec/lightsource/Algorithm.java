package cat.irec.lightsource;

import android.R.bool;
import android.util.Log;


public final class Algorithm {

	private final static double [] cie1931_2d_matching_function_x = {1.3680000e-03, 2.2360000e-03, 4.2430000e-03, 7.6500000e-03, 1.4310000e-02, 2.3190000e-02, 4.3510000e-02, 7.7630000e-02, 1.3438000e-01,
			   												  		 2.1477000e-01, 2.8390000e-01, 3.2850000e-01, 3.4828000e-01, 3.4806000e-01, 3.3620000e-01, 3.1870000e-01, 2.9080000e-01, 2.5110000e-01,
			   												  		 1.9536000e-01, 1.4210000e-01, 9.5640000e-02, 5.7950010e-02, 3.2010000e-02, 1.4700000e-02, 4.9000000e-03, 2.4000000e-03, 9.3000000e-03,
			   												  		 2.9100000e-02, 6.3270000e-02, 1.0960000e-01, 1.6550000e-01, 2.2574990e-01, 2.9040000e-01, 3.5970000e-01, 4.3344990e-01, 5.1205010e-01,
			   												  		 5.9450000e-01, 6.7840000e-01, 7.6210000e-01, 8.4250000e-01, 9.1630000e-01, 9.7860000e-01, 1.0263000e+00, 1.0567000e+00, 1.0622000e+00,
			   												  		 1.0456000e+00, 1.0026000e+00, 9.3840000e-01, 8.5444990e-01, 7.5140000e-01, 6.4240000e-01, 5.4190000e-01, 4.4790000e-01, 3.6080000e-01,
			   												  		 2.8350000e-01, 2.1870000e-01, 1.6490000e-01, 1.2120000e-01, 8.7400000e-02, 6.3600000e-02, 4.6770000e-02, 3.2900000e-02, 2.2700000e-02,
			   												  		 1.5840000e-02, 1.1359160e-02, 8.1109160e-03, 5.7903460e-03, 4.1094570e-03, 2.8993270e-03, 2.0491900e-03, 1.4399710e-03, 0.0000000e+00,
			   												  		 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00 };
	
	private final static double [] cie1931_2d_matching_function_y = {3.9000000e-05, 6.4000000e-05, 1.2000000e-04, 2.1700000e-04, 3.9600000e-04, 6.4000000e-04, 1.2100000e-03, 2.1800000e-03, 4.0000000e-03,
															  		 7.3000000e-03, 1.1600000e-02, 1.6840000e-02, 2.3000000e-02, 2.9800000e-02, 3.8000000e-02, 4.8000000e-02, 6.0000000e-02, 7.3900000e-02,
															  		 9.0980000e-02, 1.1260000e-01, 1.3902000e-01, 1.6930000e-01, 2.0802000e-01, 2.5860000e-01, 3.2300000e-01, 4.0730000e-01, 5.0300000e-01,
															  		 6.0820000e-01, 7.1000000e-01, 7.9320000e-01, 8.6200000e-01, 9.1485010e-01, 9.5400000e-01, 9.8030000e-01, 9.9495010e-01, 1.0000000e+00,
															  		 9.9500000e-01, 9.7860000e-01, 9.5200000e-01, 9.1540000e-01, 8.7000000e-01, 8.1630000e-01, 7.5700000e-01, 6.9490000e-01, 6.3100000e-01,
															  		 5.6680000e-01, 5.0300000e-01, 4.4120000e-01, 3.8100000e-01, 3.2100000e-01, 2.6500000e-01, 2.1700000e-01, 1.7500000e-01, 1.3820000e-01,
															  		 1.0700000e-01, 8.1600000e-02, 6.1000000e-02, 4.4580000e-02, 3.2000000e-02, 2.3200000e-02, 1.7000000e-02, 1.1920000e-02, 8.2100000e-03,
															  		 5.7230000e-03, 4.1020000e-03, 2.9290000e-03, 2.0910000e-03, 1.4840000e-03, 1.0470000e-03, 7.4000000e-04, 5.2000000e-04, 0.0000000e+00,
															  		 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00 };
	
	private final static double [] cie1931_2d_matching_function_z = {6.4500010e-03, 1.0549990e-02, 2.0050010e-02, 3.6210000e-02, 6.7850010e-02, 1.1020000e-01, 2.0740000e-01, 3.7130000e-01, 6.4560000e-01,
			   												  		 1.0390501e+00, 1.3856000e+00, 1.6229600e+00, 1.7470600e+00, 1.7826000e+00, 1.7721100e+00, 1.7441000e+00, 1.6692000e+00, 1.5281000e+00,
			   												  		 1.2876400e+00, 1.0419000e+00, 8.1295010e-01, 6.1620000e-01, 4.6518000e-01, 3.5330000e-01, 2.7200000e-01, 2.1230000e-01, 1.5820000e-01,
			   												  		 1.1170000e-01, 7.8249990e-02, 5.7250010e-02, 4.2160000e-02, 2.9840000e-02, 2.0300000e-02, 1.3400000e-02, 8.7499990e-03, 5.7499990e-03,
			   												  		 3.9000000e-03, 2.7499990e-03, 2.1000000e-03, 1.8000000e-03, 1.6500010e-03, 1.4000000e-03, 1.1000000e-03, 1.0000000e-03, 8.0000000e-04,
			   												  		 6.0000000e-04, 3.4000000e-04, 2.4000000e-04, 1.9000000e-04, 1.0000000e-04, 4.9999990e-05, 3.0000000e-05, 2.0000000e-05, 1.0000000e-05,
			   												  		 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00,
			   												  		 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00,
			   												  		 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00, 0.0000000e+00 };

	
	/* ********************************************************************
	 * Input: RGB values 0 to 1               							  *
	 * Output: X Y Z values in array          							  *
	 * ****************************************************************** */
	public static double[] RGBtoXYZ(double [] RGB) {
		double[] XYZ = new double[3];
		// Gamma correction	D65 Iluminant	
		RGB[0] = RGB[0] > 0.04045 ? Math.pow((RGB[0]+0.055)/1.055, 2.4) : RGB[0]/12.92;
		RGB[1] = RGB[1] > 0.04045 ? Math.pow((RGB[1]+0.055)/1.055, 2.4) : RGB[1]/12.92;
		RGB[2] = RGB[2] > 0.04045 ? Math.pow((RGB[2]+0.055)/1.055, 2.4) : RGB[2]/12.92;				
		// Linear transformation
		XYZ[0] = RGB[0] * 0.412424 + RGB[1] * 0.357579 + RGB[2] * 0.180464;
		XYZ[1] = RGB[0] * 0.212656 + RGB[1] * 0.715158 + RGB[2] * 0.072186;
		XYZ[2] = RGB[0] * 0.019332 + RGB[1] * 0.119193 + RGB[2] * 0.950444;
		// --
		XYZ[0] *= 255;
		XYZ[1] *= 255;
		XYZ[2] *= 255;
		return XYZ;
	}

	/* ********************************************************************
	 * Input: Spectro values from 380nm to 780nm in 5nm steps = 81 values *
	 * Hardcoded files: cie1931_2dg_matching_function_xyz                 *
	 * Output: X Y Z values in array                                      *
	 * ****************************************************************** */
	public static double[] SPDtoXYZ(double [] SPD) {
		double[] XYZ = new double[3];
		XYZ[0] = XYZ[1] = XYZ[2] = 0;
		for(int c = 0; c < 81; c++) {
			XYZ[0] += SPD[c] * cie1931_2d_matching_function_x[c] * 5; //dx = 1nm 
			XYZ[1] += SPD[c] * cie1931_2d_matching_function_y[c] * 5; //dx = 1nm
			XYZ[2] += SPD[c] * cie1931_2d_matching_function_z[c] * 5; //dx = 1nm
		}
		return XYZ;
	}
	
	/* ********************************************************************
	 * Input: Color Temperature in Kelvins                                *
	 * Hardcoded: 														  *
	 * 				2*h*c^2 = 37413 W*um^4*cm^-2                 		  *
	 * 				h*c/k   = 14388 um*K								  *
	 * 				formula = 2*h*c^2/Landa^5 * 1/(e^(h*c/Landa*k*T) - 1) *
	 * 				Landa in um instead of nm                             *							
	 *              c = speed of light = 3 * 10^10 cm/sec                 *
	 *              k = Boltzman constant = 1.38 * 10^-16 erg/sec         *
	 *              h = Planck constant = 6.625 * 10^-27 erg/sec          *
	 * Output: BalckBody Spectrum                                         *
	 * ****************************************************************** */
	public static double[] BlackBody(int CCT) {
		double[] SPD = new double[81];
		for (int c = 0; c < 81; c++) 
			SPD[c] = 37413 / ( Math.pow((c*0.005 + 0.38),5) * (Math.exp(14388/((c*0.005 + 0.38)*CCT)) - 1) ); 
		return SPD;
	}
	
	/* ********************************************************************
	 * Input: X Y Z values in array          							  *
	 * Output: RGB values 0 to 1               							  * 
	 * ****************************************************************** */
	public static double[] XYZtoRGB(double [] XYZ) {
		// Get xyz
		XYZ[0] /= (XYZ[0] + XYZ[1] + XYZ[2]);
		XYZ[1] /= (XYZ[0] + XYZ[1] + XYZ[2]);
		XYZ[2] /= (XYZ[0] + XYZ[1] + XYZ[2]);
		// Linear transformation
		double [] RGB = new double[3];
		RGB[0] = (XYZ[0]) *  3.2406 + (XYZ[1]) * -1.5372 + (XYZ[2]) * -0.4986;
		RGB[1] = (XYZ[0]) * -0.9689 + (XYZ[1]) *  1.8758 + (XYZ[2]) *  0.0415;
		RGB[2] = (XYZ[0]) *  0.0557 + (XYZ[1]) * -0.2040 + (XYZ[2]) *  1.0570;
		// Gamma correction (a = 0.055; gamma = 2.4)
		RGB[0] = RGB[0] > 0.0031308 ? 1.055 * Math.pow(RGB[0], 1 / 2.4) - 0.055 : 12.92 * RGB[0];
		RGB[1] = RGB[1] > 0.0031308 ? 1.055 * Math.pow(RGB[1], 1 / 2.4) - 0.055 : 12.92 * RGB[1];
		RGB[2] = RGB[2] > 0.0031308 ? 1.055 * Math.pow(RGB[2], 1 / 2.4) - 0.055 : 12.92 * RGB[2];
		// Compress the values to 0..1
		for (int c = 0; c < 3; c++) {
			if (RGB[c] < 0) RGB[c] = 0;
			if (RGB[c] > 1) RGB[c] = 1;
		}
		return RGB;
	} 

	/* ********************************************************************
	 * Input: X Y Z values in array          							  *
	 * Output: x y values as x = X / (X+Y+Z) and y = Y / (X+Y+Z)          * 
	 * ****************************************************************** */
	public static double[] XYZtoxy(double [] XYZ) {
		double [] xy = new double [2];
		xy[0] = XYZ[0] / (XYZ[0]+XYZ[1]+XYZ[2]); 
		xy[1] = XYZ[1] / (XYZ[0]+XYZ[1]+XYZ[2]);
		return xy;
	}
	
	/* ********************************************************************
	 * Input: x y values in array          								  *
	 * Output: u v values in array								          * 
	 * ****************************************************************** */
	public static double[] uvtoxy(double [] uv) {
		double [] xy = new double[2];
		xy[0] = 3*uv[0]/(2*uv[0] - 8*uv[1] + 4);
		xy[1] = 2*uv[1]/(2*uv[0] - 8*uv[1] + 4);
		return xy;
	}

	/* ********************************************************************
	 * Input: u v values in array          								  *
	 * Output: x y values in array								          * 
	 * ****************************************************************** */
	public static double[] xytouv(double [] xy) {
		double [] uv = new double[2];
		uv[0] = 4 * xy[0] / (-2*xy[0] + 12*xy[1] + 3);
		uv[1] = 6 * xy[1] / (-2*xy[0] + 12*xy[1] + 3);  
		return uv;
	}

	/* ********************************************************************
	 * Input: CCT Color temperature       								  *
	 * Output: RGB array										          * 
	 * ****************************************************************** */
	public static double[] CCTtoRGB(int CCT) {
		double [] blackbody = BlackBody(CCT);
		double [] XYZ = SPDtoXYZ(blackbody);
		double [] RGB = XYZtoRGB(XYZ);
		return RGB;
	}

	/* ********************************************************************
	 * Input: XYZ in array 			       								  *
	 * Output: CCT Color temperature in Kelvins					          * 
	 * ****************************************************************** */	
	public static int XYZtoCCT(double [] XYZ) {
		double [] xy = XYZtoxy(XYZ);
		double [] uv = xytouv(xy);
		double [] uvn = new double[2];
		double error, e = Double.MAX_VALUE;
		int CCT = 500;
		for (int c = 500; c < 8000; c=c+10) {
			uvn = xytouv(XYZtoxy(SPDtoXYZ(BlackBody(c))));
			error = Math.sqrt(Math.pow((uvn[0] - uv[0]), 2) + Math.pow((uvn[1] - uv[1]), 2));
			if (error < e) {
				e = error;
				CCT = c;
			}
		}
		return CCT;
	}
	
	/* ********************************************************************
	 * Input: 	Coefficients [0..2^Resolution] in array 				  *
	 * 			led array win values of every channel spectrum			  *
	 * 			number of channels										  *
	 * 			input coefficients resolution							  *
	 * Output: 	Result spectrum				          					  * 
	 * ****************************************************************** */
	public static double [] CoefftoSPD(int [] coef, double[][] led, int channels, int resolution) {
		double [] SPD = new double[81];
		for (int j = 0; j < 81; j++) SPD[j] = 0;
		for (int j = 0; j < 81; j++)
		for (int i = 0; i < channels; i ++)
			SPD[j] = SPD[j] + led[i][j]*((double)coef[i]/(Math.pow(2, resolution)-1));
		return SPD;
	}

	/* ********************************************************************
	 * Input: 	RGB normalized 0 to 1					 				  *
	 * Output: 	Integer Color = ALFA(8bit) Red Green Blue 8bits Channels  * 
	 * ****************************************************************** */
	public static int RGBtoColor(double [] RGB) {
		int color = 0;
		color |= (int) Math.round(RGB[0]*255);
		color <<= 8;
		color |= (int) Math.round(RGB[1]*255);
		color <<= 8;
		color |= (int) Math.round(RGB[2]*255);
		return color;
	}
	
	/* ********************************************************************
	 * Input: 	xy in array 			       							  *
	 * 			led array win values of every channel spectrum			  *
	 * 			number of channels										  *
	 * 			output coefficients resolution							  *
	 * Output: 	Coefficients per every channel					          * 
	 * ****************************************************************** */
	public static int [] xytoCoeff(double [] xy, double[][] led, int channels, int resolution) {
		double [] SPD = new double[81];
		double [] coef = new double[channels];
		double [] coefn = new double[channels];
		double [] xyn = new double[2];
		int [] retorno = new int[channels];
		double error, e;
		int channelWin;
		// --
		for (int c = 0; c < channels; c++) coef[c] = 1;
		// --
		for (int c = 0; c < 500; c++) {
			e = Double.MAX_VALUE;
			channelWin = 0;
			for (int k = 0; k < channels; k++) {
				// Recover original coefficients
				for (int j = 0; j < channels; j++) coefn[j] = coef[j];
				// Modify one coefficient to check
				coefn[k] *= 0.75;
				// Calculate new spectrum
				for (int j = 0; j < 81; j++) SPD[j] = 0;
				for (int j = 0; j < 81; j++)
					for (int i = 0; i < channels; i ++)
						SPD[j] = SPD[j] + led[i][j] * coefn[i] * 5; // TODO: Multiply by 5nm  
				// Calculate new xy coordinates
				xyn = XYZtoxy(SPDtoXYZ(SPD));
				// Calculate the distance
				error = Math.sqrt(Math.pow((xyn[0] - xy[0]), 2) + Math.pow((xyn[1] - xy[1]), 2));
				// Check if it is the minimum one
				if (error < e) {
					e = error;
					channelWin = k;
				}
			}
			// Modify the original coefficient with the winner
		    coef[channelWin] *= 0.75; 
			// Check the error
		    if (e < 0.005) break; 
		}
		for (int c = 0; c < channels; c++) retorno[c] = (int)Math.round(coef[c] * (Math.pow(2,resolution)-1));
		return retorno;
	}
	
	/* ********************************************************************
	 * Input: 	XYZ in array 			       							  *
	 * 			led array win values of every channel spectrum			  *
	 * 			number of channels										  *
	 * 			output coefficients resolution							  *
	 * Output: 	Coefficients per every channel					          * 
	 * ****************************************************************** */
	public static int [] XYZtoCoeff(double [] XYZ, double[][] led, int channels, int resolution) {
		double [][] Mxyz = new double[3][3];
		double [][] InvMxyz = null;
		int [] retorno = new int[channels];
		for (int i = 0; i < channels; i++) retorno[i] = 0;
		double A, A1, A2, A3;
		// -- Extract xy target values from XYZ
		double [] xy = XYZtoxy(XYZ);
		// -- Get all possible combinations = n! / ( (n-k)! * k!) -> n = 12 and k = 3 => num = 220 
		int index = 0;
		int numcomb = factorial(channels) / ( factorial(channels - 3) * 6);   
		int [][] combinations = new int[numcomb][3];
		int [] comb = new int[3];
		// First combination
		for (int i = 0; i < 3; ++i) comb[i] = i;
		for (int i = 0; i < 3; ++i) combinations[index][i] = comb[i];
		index++;
		// Next combination
		while (next_comb(comb, 3, channels)) {
			for (int i = 0; i < 3; ++i) combinations[index][i] = comb[i];
			index++;
		}
		// -- Get XYZ values from each channel
		double [][] XYZn = new double [channels][3];
		double [] SPD = new double[81];
		for (int c = 0; c < channels; c++) {
			for (int j = 0; j < 81; j++) SPD[j] = led[c][j] * 5; // dx = 1nm
			XYZn[c] = SPDtoXYZ(SPD);
		}
		// -- Main loop
		for (int i = 0; i < channels; i++) retorno[i] = 0;
		index = 0;
		while (index < numcomb) {
			if (combinations[index] != null) {
				// -- Get a group of three channels and extract xy values of each one
				double[] 	xy1 = XYZtoxy(XYZn[combinations[index][0]]),
							xy2 = XYZtoxy(XYZn[combinations[index][1]]),
							xy3 = XYZtoxy(XYZn[combinations[index][2]]);
				// -- Check if the target xy is inside of the triangle
				A  = triangle_area (xy1[0], xy1[1], xy2[0], xy2[1], xy3[0], xy3[1]); 	// Get area of ABC triangle
				A1 = triangle_area (xy[0],  xy[1],  xy2[0], xy2[1], xy3[0], xy3[1]);	// Get area of PBC triangle   
				A2 = triangle_area (xy1[0], xy1[1], xy[0],  xy[1],  xy3[0], xy3[1]); 	// Get area of PAC triangle  
				A3 = triangle_area (xy1[0], xy1[1], xy2[0], xy2[1], xy[0],  xy[1]); 	// Get area of PAB triangle   
				// Check if sum of A1, A2 and A3 is same as A
				if (A == (A1 + A2 + A3)) {
					// Get the solutions of the equation Xt = X1*A1 + X2*A2 + X3*A3; Yt = Y1*A1 + Y2*A2 + Y3*A3; Zt = Z1*A1 + Z2*A2 + Z3*A3;
					
					Log.d("kk"," combinations[index][0]: "+combinations[index][0]+
							   " combinations[index][1]: "+combinations[index][1]+
							   " combinations[index][2]: "+combinations[index][2]);
					
					
				
					
					Mxyz[0] = XYZn[combinations[index][0]];
					Mxyz[1] = XYZn[combinations[index][1]];
					Mxyz[2] = XYZn[combinations[index][2]];

					
					Log.d("kk"," Mxyz[0][0]: "+Mxyz[0][0]+" Mxyz[0][1]: "+Mxyz[0][1]+" Mxyz[0][2]: "+Mxyz[0][2]+
						       " Mxyz[1][0]: "+Mxyz[1][0]+" Mxyz[1][1]: "+Mxyz[1][1]+" Mxyz[1][2]: "+Mxyz[1][2]+
						       " Mxyz[2][0]: "+Mxyz[2][0]+" Mxyz[2][1]: "+Mxyz[2][1]+" Mxyz[2][2]: "+Mxyz[2][2]);
					
					
					
					InvMxyz = inv_matrix_3x3(Mxyz); // X*A = Y -> A = Y/X
					if (InvMxyz != null) {
						
						/*
						Log.d("kk","sol[0]: "+(InvMxyz[0][0] * XYZ[0] + InvMxyz[0][1] * XYZ[1] + InvMxyz[0][2] * XYZ[2]));
						Log.d("kk","sol[1]: "+(InvMxyz[1][0] * XYZ[0] + InvMxyz[1][1] * XYZ[1] + InvMxyz[1][2] * XYZ[2]));
						Log.d("kk","sol[2]: "+(InvMxyz[2][0] * XYZ[0] + InvMxyz[2][1] * XYZ[1] + InvMxyz[2][2] * XYZ[2]));*/

						retorno[combinations[index][0]] = (int)(InvMxyz[0][0] * XYZ[0] + InvMxyz[0][1] * XYZ[1] + InvMxyz[0][2] * XYZ[2]);
						retorno[combinations[index][1]] = (int)(InvMxyz[1][0] * XYZ[0] + InvMxyz[1][1] * XYZ[1] + InvMxyz[1][2] * XYZ[2]);
						retorno[combinations[index][2]] = (int)(InvMxyz[2][0] * XYZ[0] + InvMxyz[2][1] * XYZ[1] + InvMxyz[2][2] * XYZ[2]);
						Log.d("retorno","a "+retorno[combinations[index][0]]+" b "+retorno[combinations[index][1]]+" c "+retorno[combinations[index][2]]);
						// Remove combinations with these channels
						if (index < numcomb-1) 
							for (int i = index+1; i < numcomb; i++)
								if (combinations[i] != null)
									if ((combinations[i][0] == combinations[index][0]) || (combinations[i][0] == combinations[index][1]) || (combinations[i][0] == combinations[index][2]) || 
										(combinations[i][1] == combinations[index][0]) || (combinations[i][1] == combinations[index][1]) || (combinations[i][1] == combinations[index][2]) ||
										(combinations[i][2] == combinations[index][0]) || (combinations[i][2] == combinations[index][1]) || (combinations[i][2] == combinations[index][2]) ) 
											combinations[i] = null;
						combinations[index] = null;
					}
				}
			}
			index++;
		}
		// -- Normalize results
		index = 0;
		for (int c = 0; c < channels; c++) if (retorno[c] > index) index = retorno[c];
		for (int c = 0; c < channels; c++) retorno[c] = (int)Math.round((retorno[c] * (Math.pow(2,resolution)-1))/index);
		return retorno;
	}

	public static int factorial(int n) {
		if (n <= 20) return (1 > n) ? 1 : n * factorial(n - 1);
	    return 0; // If number is higher than 20 
	}

	public static boolean next_comb(int comb[], int k, int n) {
		int i = k - 1;
		++comb[i];
		while ((i > 0) && (comb[i] >= n - k + 1 + i)) {
			--i;
			++comb[i];
		}
		// -- Combination (n-k, n-k+1, ..., n) reached 
		if (comb[0] > n - k) return false; //  No more combinations can be generated 
		// -- comb now looks like (..., x, n, n, n, ..., n). Turn it into (..., x, x + 1, x + 2, ...) 
		for (i = i + 1; i < k; ++i)	comb[i] = comb[i - 1] + 1;
		return true;
	}
	
	public static double triangle_area(double xy1, double xy12, double xy2, double xy22, double xy3, double xy32) {
	  	return Math.abs((xy1*(xy22-xy32) + xy2*(xy32-xy12)+ xy3*(xy12-xy22))/2.0);
	}
	
	public static double [][] inv_matrix_3x3(double[][] A) {
		double determinant = +A[0][0]*(A[1][1]*A[2][2]-A[2][1]*A[1][2])
							 -A[0][1]*(A[1][0]*A[2][2]-A[1][2]*A[2][0])
							 +A[0][2]*(A[1][0]*A[2][1]-A[1][1]*A[2][0]);
		double [][] Matrix = null;
		double invdet;
		if (determinant != 0.0) {
			invdet = 1.0/determinant;
			Matrix = new double[3][3];
			Matrix[0][0] =  (A[1][1]*A[2][2]-A[2][1]*A[1][2])*invdet;
			Matrix[1][0] = -(A[0][1]*A[2][2]-A[0][2]*A[2][1])*invdet;
			Matrix[2][0] =  (A[0][1]*A[1][2]-A[0][2]*A[1][1])*invdet;
			Matrix[0][1] = -(A[1][0]*A[2][2]-A[1][2]*A[2][0])*invdet;
			Matrix[1][1] =  (A[0][0]*A[2][2]-A[0][2]*A[2][0])*invdet;
			Matrix[2][1] = -(A[0][0]*A[1][2]-A[1][0]*A[0][2])*invdet;
			Matrix[0][2] =  (A[1][0]*A[2][1]-A[2][0]*A[1][1])*invdet;
			Matrix[1][2] = -(A[0][0]*A[2][1]-A[2][0]*A[0][1])*invdet;
			Matrix[2][2] =  (A[0][0]*A[1][1]-A[1][0]*A[0][1])*invdet;
		}
		return Matrix;
	}
}
