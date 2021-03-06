//
//  MaskView.swift
//  作用：半圆弧view
//  PointExchange
//
//  Created by yiner on 2018/7/17.
//  Copyright © 2018年 WannaWin. All rights reserved.
//

import UIKit

class MaskView: UIView {

	override func draw(_ rect: CGRect) {
		// 画半弧形蒙版
		let color = UIColor.white
		color.set() // 设置线条颜色
		
		let width = self.bounds.size.width
		let height = self.bounds.size.height
		let h = 1*height/15
		let d = (width/2)/CGFloat(tan(15*Double.pi/180))
		let center = CGPoint(x: width/2 , y: d + h)
		let radius = (width/2)/CGFloat(sin(15*Double.pi/180))
		
		let path = UIBezierPath(arcCenter: center, radius: radius , startAngle:(CGFloat)(19*Double.pi/12), endAngle: (CGFloat)(17*Double.pi/12), clockwise:false)
		
		path.addLine(to: CGPoint(x:0, y:height))
		path.addLine(to: CGPoint(x:width, y:height))
		path.addLine(to: CGPoint(x:width, y:h))
		
		path.close()
		path.lineWidth = 5.0
		path.fill()
	}

}
