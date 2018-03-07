//
//  ACRRenderer.mm
//  ACRRenderer.h
//
//  Copyright © 2017 Microsoft. All rights reserved.
//
#import "ACOAdaptiveCardPrivate.h"
#import "ACOBaseActionElementPrivate.h"
#import "ACOBaseCardElementPrivate.h"
#import "ACOHostConfigPrivate.h"
#import "ACRBaseCardElementRenderer.h"
#import "ACRBaseActionElementRenderer.h"
#import "ACRColumnSetView.h"
#import "ACRColumnView.h"
#import "ACRRegistration.h"
#import "ACRRendererPrivate.h"
#import "ACRSeparator.h"
#import "ACRViewControllerPrivate.h"

using namespace AdaptiveCards;

@implementation ACRRenderer

- (instancetype)init
{
    self = [super init];
    return self;
}

// This interface is exposed to outside, and returns ACRRenderResult object
// This object contains a viewController instance which defer rendering adaptiveCard untill viewDidLoad is called.
+ (ACRRenderResult *)render:(ACOAdaptiveCard *)card config:(ACOHostConfig *)config frame:(CGRect)frame rootViewController:(UIViewController *)rootViewController
{
    ACRRenderResult *result = [[ACRRenderResult alloc] init];

    // Initializes ACRViewController instance with HostConfig and AdaptiveCard
    // ACRViewController does not render adaptiveCard untill viewDidLoad calls render
    ACRViewController *viewcontroller = [[ACRViewController alloc] init:card
                                                             hostconfig:config
                                                                  frame:frame
                                                     rootViewController:rootViewController];

    result.viewcontroller = viewcontroller;
    result.succeeded = YES;
    return result;
}
// transforms (i.e. renders) an adaptiveCard to a new UIView instance
+ (UIView *)renderWithAdaptiveCards:(std::shared_ptr<AdaptiveCard> const &)adaptiveCard
                             inputs:(NSMutableArray *)inputs
                     viewController:(UIView *)vc
                         guideFrame:(CGRect)guideFrame
                         hostconfig:(ACOHostConfig *)config
{
    std::vector<std::shared_ptr<BaseCardElement>> body = adaptiveCard->GetBody();

    ACRColumnView *verticalView = nil;

    if(!body.empty())
    {
        [(ACRViewController *)vc addTasksToConcurrentQueue:body];

        verticalView = [[ACRColumnView alloc] initWithFrame:CGRectMake(0, 0, guideFrame.size.width, guideFrame.size.height)];
        ACRContainerStyle style = ([config getHostConfig]->adaptiveCard.allowCustomStyle)? (ACRContainerStyle)adaptiveCard->GetStyle() : ACRDefault;
        style = (style == ACRNone)? ACRDefault : style;
        [verticalView setStyle:style];
    
        [ACRRenderer render:verticalView rootViewController:vc inputs:inputs withCardElems:body andHostConfig:config];

        [[(ACRViewController *)vc card] setInputs:inputs];

        std::vector<std::shared_ptr<BaseActionElement>> actions = adaptiveCard->GetActions();

        [ACRSeparator renderActionsSeparator:verticalView hostConfig:[config getHostConfig]];
        // renders buttons and their associated actions
        UIView<ACRIContentHoldingView> *actionChildView = [ACRRenderer renderButton:vc inputs:inputs superview:verticalView actionElems:actions hostConfig:config];
        [verticalView addArrangedSubview:actionChildView];
    }
    return verticalView;
}

+ (UIView<ACRIContentHoldingView> *)renderButton:(UIView *)vc
                                          inputs:(NSMutableArray *)inputs
                                       superview:(UIView<ACRIContentHoldingView> *)superview
                                     actionElems:(std::vector<std::shared_ptr<BaseActionElement>> const &)elems
                                      hostConfig:(ACOHostConfig *)config
{
    ACRRegistration *reg = [ACRRegistration getInstance];
    UIView<ACRIContentHoldingView> *childview = nil;
    UILayoutConstraintAxis axis = UILayoutConstraintAxisVertical;
    if(ActionsOrientation::Horizontal == [config getHostConfig]->actions.actionsOrientation)
    {
        childview = [[ACRColumnSetView alloc] initWithFrame:CGRectMake(0, 0, superview.frame.size.width, superview.frame.size.height)];
        axis = UILayoutConstraintAxisHorizontal;
    }
    else
    {
        childview = [[ACRColumnView alloc] initWithFrame:CGRectMake(0, 0, superview.frame.size.width, superview.frame.size.height)];
    }

    ACOBaseActionElement *acoElem = [[ACOBaseActionElement alloc] init];

    for(const auto &elem:elems)
    {
        ACRBaseActionElementRenderer *actionRenderer =
        [reg getActionRenderer:[NSNumber numberWithInt:(int)elem->GetElementType()]];

        if(actionRenderer == nil)
        {
            NSLog(@"Unsupported card action type:%d\n", (int) elem->GetElementType());
            continue;
        }

        [acoElem setElem:elem];

        UIButton* button = [actionRenderer renderButton:vc
                                                 inputs:inputs
                                              superview:superview
                                      baseActionElement:acoElem
                                             hostConfig:config];
        [childview addArrangedSubview:button];

        [ACRSeparator renderSeparationWithFrame:CGRectMake(0,0,[config getHostConfig]->actions.buttonSpacing, [config getHostConfig]->actions.buttonSpacing)
                                      superview:childview axis:axis];
    }

    [childview adjustHuggingForLastElement];

    return childview;
}

+ (UIView *)render:(UIView<ACRIContentHoldingView> *)view
            rootViewController:(UIView *)vc
            inputs:(NSMutableArray *)inputs
     withCardElems:(std::vector<std::shared_ptr<BaseCardElement>> const &)elems
     andHostConfig:(ACOHostConfig *)config
{
    ACRRegistration *reg = [ACRRegistration getInstance];
    ACOBaseCardElement *acoElem = [[ACOBaseCardElement alloc] init];

    for(const auto &elem:elems)
    {
        [ACRSeparator renderSeparation:elem forSuperview:view withHostConfig:[config getHostConfig]];

        ACRBaseCardElementRenderer *renderer =
            [reg getRenderer:[NSNumber numberWithInt:(int)elem->GetElementType()]];

        if(renderer == nil)
        {
            NSLog(@"Unsupported card element type:%d\n", (int) elem->GetElementType());
            continue;
        }

        [acoElem setElem:elem];
        [renderer render:view rootViewController:vc inputs:inputs baseCardElement:acoElem hostConfig:config];
    }

    return view;
}
@end
