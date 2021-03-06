package io.adaptivecards.renderer;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import io.adaptivecards.objectmodel.ActionAlignment;
import io.adaptivecards.objectmodel.ActionsOrientation;
import io.adaptivecards.objectmodel.BaseActionElement;
import io.adaptivecards.objectmodel.BaseActionElementVector;
import io.adaptivecards.objectmodel.HostConfig;
import io.adaptivecards.objectmodel.IconPlacement;
import io.adaptivecards.objectmodel.Spacing;
import io.adaptivecards.renderer.AdaptiveWarning;
import io.adaptivecards.renderer.BaseCardElementRenderer;
import io.adaptivecards.renderer.IActionLayoutRenderer;
import io.adaptivecards.renderer.IBaseActionElementRenderer;
import io.adaptivecards.renderer.RenderedAdaptiveCard;
import io.adaptivecards.renderer.actionhandler.ICardActionHandler;
import io.adaptivecards.renderer.registration.CardRendererRegistration;

public class ActionLayoutRenderer implements IActionLayoutRenderer {

    protected ActionLayoutRenderer()
    {
    }

    public static ActionLayoutRenderer getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new ActionLayoutRenderer();
        }

        return s_instance;
    }

    public void renderActions(RenderedAdaptiveCard renderedCard, Context context, FragmentManager fragmentManager, ViewGroup viewGroup, BaseActionElementVector baseActionElementList, ICardActionHandler cardActionHandler, HostConfig hostConfig) {
        long size;
        if (baseActionElementList == null || (size = baseActionElementList.size()) <= 0)
        {
            return;
        }

        LinearLayout actionButtonsLayout = new LinearLayout(context);
        actionButtonsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int alignment = hostConfig.getActions().getActionAlignment().swigValue();
        if (alignment == ActionAlignment.Right.swigValue())
        {
            actionButtonsLayout.setGravity(Gravity.RIGHT);
        }
        else if (alignment == ActionAlignment.Center.swigValue())
        {
            actionButtonsLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        int actionButtonsLayoutOrientation = hostConfig.getActions().getActionsOrientation().swigValue();
        if (actionButtonsLayoutOrientation == ActionsOrientation.Vertical.swigValue())
        {
            actionButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        }
        else
        {
            actionButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        }

        Spacing spacing = hostConfig.getActions().getSpacing();
        /* Passing false for separator since we do not have any configuration for separator in actionsConfig */
        BaseCardElementRenderer.setSpacingAndSeparator(context, viewGroup, spacing, false, hostConfig, true /* Horizontal Line */);

        if (viewGroup != null)
        {
            if(actionButtonsLayoutOrientation == ActionsOrientation.Horizontal.swigValue())
            {
                HorizontalScrollView actionButtonsContainer = new HorizontalScrollView(context);
                actionButtonsContainer.setHorizontalScrollBarEnabled(false);
                actionButtonsContainer.addView(actionButtonsLayout);
                viewGroup.addView(actionButtonsContainer);
            }
            else
            {
                viewGroup.addView(actionButtonsLayout);
            }
        }

        int i = 0;
        long maxActions = hostConfig.getActions().getMaxActions();

        boolean allActionsHaveIcons = true;
        for(; i < size && i < maxActions; ++i)
        {
            BaseActionElement actionElement = baseActionElementList.get(i);
            if(actionElement.GetIconUrl().isEmpty())
            {
                allActionsHaveIcons = false;
                break;
            }
        }

        for (i = 0; i < size && i < maxActions; i++)
        {
            BaseActionElement actionElement = baseActionElementList.get(i);

            IconPlacement originalIconPlacement = hostConfig.getActions().getIconPlacement();
            if(!allActionsHaveIcons)
            {
                hostConfig.getActions().setIconPlacement(IconPlacement.LeftOfTitle);
            }

            IBaseActionElementRenderer actionRenderer = CardRendererRegistration.getInstance().getActionRenderer();
            actionRenderer.render(renderedCard, context, fragmentManager, actionButtonsLayout, actionElement, cardActionHandler, hostConfig);
            hostConfig.getActions().setIconPlacement(originalIconPlacement);
        }

        if (i >= maxActions && size != maxActions)
        {
            renderedCard.addWarning(new AdaptiveWarning(AdaptiveWarning.MAX_ACTIONS_EXCEEDED, "A maximum of " + maxActions + " actions are allowed"));
        }
    }

    private static ActionLayoutRenderer s_instance = null;
}
