# smd_project
- This repository contains source codes of projects for Software Modeling and Design
- Team Members: Liguo Chen, Haichao Song, Zengbin Zhu
## Project 1
- You and your team of independent Software Contractors, have been hired by Robotic Mailing Solutions Inc.
(RMS) to provide some much needed assistance in delivering the latest version of their product Automail
to market. The Automail is an automated mail sorting and delivery system designed to operate in large
buildings that have dedicated Mail rooms. It offers end to end receipt and delivery of mail items within
the building, and can be tweaked to fit many different installation environments.
- Our grade: 10.5/12
- Feedback:  
  - Domain model: team is not presented.  
  - Class Diagram: some dependencies are missing, i.e., RobotTeam and Robot  
  - Sequence Diagram: good  
  - Report: Some arguments are incorrect, composite pattern is not well 
    established, i.e., addToTeam should be in IRobot. When talking about 
    low coulping and high cohesion, more justifications about why low/high 
    are desired  
  - Implementation: pass all tests. implementation is reasonable.
## Project 2
- Robotic Mailing Solutions Inc. (RMS) were so happy with your changes 
and design report for the Automail simulation system, they saw the 
opportunity to apply your skills to a more challenging task. Wanting to 
break out of the bounds of delivering only within high rise buildings, 
they decided to jump on the autonomous vehicle bandwagon and start a 
parcel service. Right from the start, they recognised that, if they 
want to go global with their solution, they will need to support different
approaches that recognise local conditions.
- Our grade: 16/20
- Feedback:
  - Design class diagram: Generally good. Should use "1" on singleton 
    class. The watermarks makes the diagram really hard to read (800% zoom). 
  - Communication diagram: Good use of "ref" frame and smaller diagrams. 
    The watermarks makes the diagram really hard to read (800% zoom). 
  - Report: Clear structure.  
    Good discussion and judgment on adapter on 
    tille for a stable interface.  
    Good discussion on Composite being not useful as others.  
    Can include diagrams/codes while discussing the design choice. 
  - Implementation: Solved most of maps.  
    Good decision to separating domain strategies from path finding algorithms.  
    Good approriate use of factory, adapter pattern.  
    Your team is right when discussing about the composite strategy being not useful. 
    In fact, for more complex cases, it decreases the flexibility and increase complexity. 
    Thus, your team can get rid of it in further refactoring.  
    Also further improvement can look at Facade, observer and decorator pattern which allows to control better of the car and its extra capabilities.